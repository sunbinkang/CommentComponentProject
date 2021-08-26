# 1. 说明

本协议为 feed 系统前端与后端通讯的业务协议，主要包含业务字段定义及说明

业务协议格式为 JSON



feed 系统所支持的业务模块包括：

我的，首页发一发，股吧，牛牛圈



## 1.1 url prefix：/feed

http://ip:port/feed/			feed 业务协议前缀



各业务 url 在此前缀后增加各自的 path



如： 

发帖：/add

完整的 url 为：http://ip:port/feed/add



## 1.2 请求格式

```json
{
    // 具体的业务请求字段
}
```



分页查询请求统一增加两个字段

```json
{
    "pageNum": 1,		// 第几页
    "pageSize": 10		// 每页记录条数
}
```



## 1.3 回应格式

参照 KOCA 提供的统一回应数据格式

```shell
{
	"code": "0",
	"msg": "请求处理成功",
	"detail": "",
	"head": {},	// 具体的业务请求head字段，一般为空
	"body": {}	// 具体的业务请求内容字段
}
```



本协议回应字段仅对 body 业务字段做说明

如无回应字段说明，则 body 为 null，仅公共字段 code，msg，detail 有值。



## 1.4 用户标识说明

用户认证成功后，认证信息在 http cookie 中传递，请求业务字段无需再带用户认证请求信息如：用户id等。



## 1.5 其它说明

字符串长度限制 	//[120]

整型字段值为 0 时，表示此字段无意义



本文中帖子，特指发一发发的视频。

股吧在客户端展现上没有帖子一说。

本文所有评论都关联某一个帖子。

股吧直接发评论，后台会自动关联一个默认的股吧帖子。



外部内容部分可以点赞，收藏，关注，后台将给外部内容分配一个 msgId，和 一个特有的 userId (0)



# 2. 修改类

## 2.1 发帖（发一发）: /add

前端仅发一发使用

request

```json
{
    "type": "0",	// 0 股吧原创帖	1 发一发原创帖
    "stockCode": "", // 市场代码_股票代码, 仅股吧有
    "title": "",	// 帖子标题 //[200]
    "content": "",	// 帖子内容 //[200]        
    "imageHeight": 1,	// int	// image发一发有，股吧没有
    "imageWidth": 1,		// int
    "imageContent": "base64content",	// image content           
    "videoDuration": 100.0,	// 视频时长 // double // video发一发有，股吧没有
    "videoId": "235"	// 视频id             	
}
```



response

```json
{
    "msgId": 1       // 消息 ID	//long	
}
```



## 2.2 删帖: /del

request

```json
{
    "msgId": 1		// 消息 ID	//long	
}
```



response

无



## 2.3 发评论: /comment

只能评论自有体系的内容。(股吧和发一发的评论)

request

```json
{
    "type": "0",        	// 0 股吧评论	1 发一发评论
    "resourceId": "",   	// type为0时为 市场代码_股票代码， type为1时为 msgId          
    "content": "",			// 评论内容 //[300]    
    "replyCommentId": 0,	// 被评论的原评论 ID , 如果为0 ，默认为对帖子的评论，即一级评论//long 
    "rootCommentId": 0      // 评论的根评论，如果为0，为一级评论
}
```



response

```json
{
	"msgId": 1,		// 评论所属的帖子 ID
	"commentId": 1,	// 评论 ID	
}
```



## 2.4 删评论: /commentdel

request

```json
{
    "type": "0",    // 0 股吧评论	1 发一发评论
	"resourceId": "",	// 股票代号 / 发一发msgId
	"commentId": 1	// 评论 ID	
}
```



response

无



## 2.5 点赞(及取消点赞)帖子或评论：/like

request

```json
{
    "type": "0",      // 0 股吧	1 发一发
    "like": "0",      // 0 点赞 1 取消点赞
 	"resourceId": "",		// 资源Id 股票代码/发一发msgId
    "commentId": 1  //  评论 ID，非 0 时点赞评论
}
```



response

无



## 2.6 [cancel] 收藏(及取消收藏)：/collect

request

```json
{
    "type": "0",		// 0 收藏 1 取消收藏
    "msgId": 1			// 帖子 ID
}
```



response

无



## 2.7 [cancel] 关注(及取消关注)：/follow

request

```json
{
    "type": 0,		// 0 关注 1 取消关注
    "userId": 1		// 被关注的用户 ID
}
```



response

无



## 2.8 牛牛圈：积分：/ggc/credit

仅供后台远程调用



request

```shell
{
	"type": 1,	// 1 等级日榜，2等级周榜，3等级月榜，4等级总榜，5积分日榜，6积分周榜，7积分月榜，7积分总榜
	"userId": 2,	// 用户 ID
	"mobile": "13100000000",	// 用户手机号
	"userName": "user name", // 用户名
	"value": 1		// 积分	 // long
}
```



response

无



# 3. 查询类

## 3.1 查帖子（查发一发的内容）

### 3.1.1 我的：发布 分页查我的帖子: /list

request

```json
{
    "pageNum": 1,		// 第几页
    "pageSize": 10		// 每页记录条数
}
```



response

```json
{
    "total": 0,
    "data":
    [
        {
            "type": "0",	// 0 股吧原创帖	1 发一发原创帖
            "stockCode": "", // 股票代码, 仅股吧有
            "title": "",	// 帖子标题 //[200]
            "content": "",	// 帖子内容 //[200]    
            "imageHeight": 1,	// int	// image发一发有，股吧没有
            "imageWidth": 1,		// int
            "imageUrl": "http://somecloud.cn/1.png",	// image url           
            "videoDuration": 100.0,	// 视频时长 // double // video发一发有，股吧没有
            "videoId": "235",	// 视频id 
            "likeCount": 10,	// 获赞数
        },
        {
            "type": "0",	// 0 股吧原创帖	1 发一发原创帖
            "stockCode": "", // 股票代码, 仅股吧有
            "title": "",	// 帖子标题 //[200]
            "content": "",	// 帖子内容 //[200]    
            "imageHeight": 1,	// int	// image发一发有，股吧没有
            "imageWidth": 1,		// int
            "imageUrl": "http://somecloud.cn/1.png",	// image url           
            "videoDuration": 100.0,	// 视频时长 // double // video发一发有，股吧没有
            "videoId": "235",	// 视频id 
            "likeCount": 10,	// 获赞数
        }
	]
}
```



### 3.1.2 [废弃] 分页查我关注的人的帖子: /followlist

仅文章帖子，无视频

见 3.1.6



request

```json
{
    "userId": 0,	// 0 查所有人的帖子 ； 非0 特定人的帖子
    "pageNum": 1,		// 第几页
    "pageSize": 10		// 每页记录条数
}
```



response

```json
{
    "total": 0,
    "data":
    [
        {
            "msgId": 1,
            "sendTime": "2021-08-02 00:00:00",
            "authorId": 10,	// 帖子发布者 ID
            "title": "",	// 帖子标题 //[200]
        },
        {
            "msgId": 2,
            "sendTime": "2021-08-02 00:00:00",
            "authorId": 10,	// 帖子发布者 ID
            "title": "",	// 帖子标题 //[200]
        }
	]
}
```



### 3.1.3 [废弃] 查某只股票的帖子 /stocklist

request

```json
{
    "stockCode": "",    // 股票代码
    "pageNum": 1,		// 第几页
    "pageSize": 10		// 每页记录条数

}
```



response

```json
{
    "total": 0,
    "data":
    [
        {
            "msgId": 1,
            "sendTime": "2021-08-02 00:00:00",
            "authorId": 10,	// 帖子发布者 ID
            "title": "",	// 帖子标题 //[200]
        },
        {
            "msgId": 2,
            "sendTime": "2021-08-02 00:00:00",
            "authorId": 10,	// 帖子发布者 ID
            "title": "",	// 帖子标题 //[200]
        }
	]
}
```



### 3.1.4 [废弃] 查帖子详情: /detail

request

```json
{
    "msgId": 1,
    "pageNum": 1,		// 第几页
    "pageSize": 10		// 每页记录条数
}
```



response

```json
{
    "title": "",	// 帖子标题 //[200]
    "content": "",	// 帖子内容 //[200]
    "sendTime": "2021-08-02 00:00:00",
    "commentCount": 2,	// 评论数
    "transferCount": 0, // 转发数
    "likeCount": 1,		// 点赞数
    "collectCount": 2,	// 收藏数
    "status": "0",		// 0正常 1审核中 2删除
    "authorId"			// 作者 ID
}
```



### 3.1.5 [废弃] 我的：分页查我收藏的帖子：/collectmsglist

包含收藏的抖音和内部的视频

request

```shell
{
    "pageNum": 1,		// 第几页
    "pageSize": 10		// 每页记录条数
}
```



response

```shell
{
	"total": 0,
	"data": {
		"ArticleList": [{		// 文章列表
			"Abstract": "",		// 文章摘要
			"Author": {			// 作者
				"AuthorAvatarUrl": "",	// 作者头像 url
				"AuthorName": "",	// 作者名
				"AuthorTags": ["authortag"]	// 作者标签
			},
			"CommentCount": 0,	// 评论条数
			"Content": "",		// 文章内容，视频则为视频链接
			"ContentCntw": 0,	// 文章字数
			"CoverImages": [{	// 封面图列表
				"Height": 128,	// 高
				"Width": 256,	// 宽
				"Url": "http://tiktok.com/some.png"	// 原始图片url
			}],
			"DiggCount": 0,			// 点赞数量
			"Extra": "msgId=99",	// 扩展信息 feed 帖子id
			"GenerateType": "", // 文章生成类型，渠道
			"GroupId": "",		// 文章组ID
			"GroupImages": [{	// 文章图片列表
				"Height": 11,
				"Width": 22,
				"Url": "http://tiktok.com/some.png"	// 原始图片url
			}],
			"GroupSource": "",
			"GroupType": "",	// 文章类型
			"GroupVideos": [{	// 文章视频列表
				"CoverImage": {	// 封面图
					"Height": 1,
					"Width": 2,
					"Url": "http://tiktok.com/some.png"	// 原始图片url
				},
				"Duration": 100.0,	// 视频时长
				"Id": "2",	// 视频id
				"Url": "http://tiktok.com/123",	// 原始视频url
				"VideoSize": 30000	// 视频文件大小
			}],
			"Link": "",				// 文章原始链接
			"OpenId": "1234",		// 用户id
			"PublishTime": 1629274516,	// 文章发布时间 秒
			"SourceName": "",			// 文章来源名称
			"Tag": ["study"],	// 文章标签，用户自定义标签
			"Tags": ["boy"],	// 文章标签，用户自定义标签
			"Title": ""			// 文章标题
		}],
		"RequestId": "123"	// 请求ID
	}
}
```



### 3.1.6 [废弃] 我的：分页查我关注的人的帖子：/careaboutlist

取代 3.1.2 

包含收藏的抖音和内部的视频或文章

request

```shell
{
    "pageNum": 1,		// 第几页
    "pageSize": 10		// 每页记录条数
}
```



response

抖音视频响应格式

```shell
{
	"total": 0,
	"data": {
		"ArticleList": [{		// 文章列表
			"Abstract": "",		// 文章摘要
			"Author": {			// 作者
				"AuthorAvatarUrl": "",	// 作者头像 url
				"AuthorName": "",	// 作者名
				"AuthorTags": ["authortag"]	// 作者标签
			},
			"CommentCount": 0,	// 评论条数
			"Content": "",		// 文章内容，视频则为视频链接
			"ContentCntw": 0,	// 文章字数
			"CoverImages": [{	// 封面图列表
				"Height": 128,	// 高
				"Width": 256,	// 宽
				"Url": "http://tiktok.com/some.png"	// 原始图片url
			}],
			"DiggCount": 0,
			"Extra": "msgId=99",	// 扩展信息 消息id
			"GenerateType": "", // 文章生成类型，渠道
			"GroupId": "",		// 文章组ID
			"GroupImages": [{	// 文章图片列表
				"Height": 11,
				"Width": 22,
				"Url": "http://tiktok.com/some.png"	// 原始图片url
			}],
			"GroupSource": "",
			"GroupType": "",	// 文章类型
			"GroupVideos": [{	// 文章视频列表
				"CoverImage": {	// 封面图
					"Height": 1,
					"Width": 2,
					"Url": "http://tiktok.com/some.png"	// 原始图片url
				},
				"Duration": 100.0,	// 视频时长
				"Id": "2",	// 视频id
				"Url": "http://tiktok.com/123",	// 原始视频url
				"VideoSize": 30000	// 视频文件大小
			}],
			"Link": "",				// 文章原始链接
			"OpenId": "1234",		// 用户id
			"PublishTime": 1629274516,	// 文章发布时间 秒
			"SourceName": "",			// 文章来源名称
			"Tag": ["study"],	// 文章标签，用户自定义标签
			"Tags": ["boy"],	// 文章标签，用户自定义标签
			"Title": ""			// 文章标题
		}],
		"RequestId": "123"	// 请求ID
	}
}
```



### 3.1.7 我的：分页查我赞过的帖子：/likemsglist

包含收藏的抖音和内部的视频

request

```shell
{
    "pageNum": 1,		// 第几页
    "pageSize": 10		// 每页记录条数
}
```



response

```shell
{
	"total": 0,
	"data": {
		"ArticleList": [{		// 文章列表
			"Abstract": "",		// 文章摘要
			"Author": {			// 作者
				"AuthorAvatarUrl": "",	// 作者头像 url
				"AuthorName": "",	// 作者名
				"AuthorTags": ["authortag"]	// 作者标签
			},
			"CommentCount": 0,	// 评论条数
			"Content": "",		// 文章内容，视频则为视频链接
			"ContentCntw": 0,	// 文章字数
			"CoverImages": [{	// 封面图列表
				"Height": 128,	// 高
				"Width": 256,	// 宽
				"Url": "http://tiktok.com/some.png"	// 原始图片url
			}],
			"DiggCount": 0,			// 点赞数量
			"Extra": "msgId=99",	// 扩展信息 消息id
			"GenerateType": "", // 文章生成类型，渠道
			"GroupId": "",		// 文章组ID
			"GroupImages": [{	// 文章图片列表
				"Height": 11,
				"Width": 22,
				"Url": "http://tiktok.com/some.png"	// 原始图片url
			}],
			"GroupSource": "",
			"GroupType": "",	// 文章类型
			"GroupVideos": [{	// 文章视频列表
				"CoverImage": {	// 封面图
					"Height": 1,
					"Width": 2,
					"Url": "http://tiktok.com/some.png"	// 原始图片url
				},
				"Duration": 100.0,	// 视频时长
				"Id": "2",	// 视频id
				"Url": "http://tiktok.com/123",	// 原始视频url
				"VideoSize": 30000	// 视频文件大小
			}],
			"Link": "",				// 文章原始链接
			"OpenId": "1234",		// 用户id
			"PublishTime": 1629274516,	// 文章发布时间 秒
			"SourceName": "",			// 文章来源名称
			"Tag": ["study"],	// 文章标签，用户自定义标签
			"Tags": ["boy"],	// 文章标签，用户自定义标签
			"Title": ""			// 文章标题
		}],
		"RequestId": "123"	// 请求ID
	}
}
```





## 3.2 查评论

### 3.2.1 分页查帖子/评论的评论：/commentlist

request



```json
{
    "type": "0",        // 0 股吧评论	1 发一发评论
    "resourceId": "",   // type 为0时为 市场代码_股票代码， type为1时为 msgId    
    "commentId": 0,     // commentId为0时为帖子的评论，即查询一级评论，非0时为查询一级评论的评论 
    "pageNum": 1,		// 第几页
    "pageSize": 10		// 每页记录条数
}
```



response

```json
{
    "total": 0,
    "data":
    [
        {
            "type": "0",			// 0 股吧评论	1 SNS评论
            "commentContent": "",			// 评论内容 //[300]
            "msgId": 1,				// 被评论的帖子 ID	//long
            "rootCommentId": 1,     // 评论的根评论  // 一级评论的根评论为0，二三级评论的根评论为一级评论的id
            "replyCommentId": 1,    // 被评论的原评论 ID  //long
            "feedMsgReplyCommentUserVo": {   // 被回复人的相关信息，可以扩展
                "replyCommenIdUserId": 1,  // 当三级评论回复二级评论时，二级评论的userId，默认为0 
                "replyCommentIdUserName": "", // 被回复人的name, 即三级回复二级时，二级评论的userName
                "sImgUrl":"",    //被回复人的小头像
            },
            "userId": 1,			// 评论者 ID
            "userName": "",   // 评论者姓名
            "userImgUrl":"",  // 评论者头像
            "likeCount": 1,			// 点赞数
            "replyTime": "2021-08-02 00:00:00",	// 评论时间
            "commentId": 1,         // 评论 Id
            "isAuthor": 1,           // 是否是作者 针对二三级评论，即2、3级评论是否与1级评论为同一个user// 1是，默认为0，不是
            "like": 1,               // 当前用户是否点赞过该评论 0：未点赞， 1：点过赞 
            "commentCount": 1       // 一级评论下二、三级评论总数,其余情况为默认值0
        },
        {
            "type": "0",			// 0 股吧评论	1 SNS评论
            "commentContent": "",			// 评论内容 //[300]
            "msgId": 1,				// 被评论的帖子 ID	//long
            "replyCommentId": 2,	// 被评论的原评论 ID  //long 
            "rootCommentId": 1,     // 评论的根评论  // 一级评论的根评论为0，二三级评论的根评论为一级评论的id
            "feedMsgReplyCommentUserVo": {   // 被回复人的相关信息，可以扩展
                "replyCommenIdUserId": 1,  // 当三级评论回复二级评论时，二级评论的userId，默认为0 
                "replyCommentIdUserName": "", // 被回复人的name, 即三级回复二级时，二级评论的userName
                "sImgUrl":"",    // 被回复人的小头像
            },
            "userId": 2,       // 评论者 ID
            "userName": "",   // 评论者姓名
            "userImgUrl":"",  // 评论者头像
            "likeCount": 1,			// 点赞数
            "replyTime": "2021-08-02 00:00:00",	// 评论时间
            "commentId": 2,          // 评论 Id
            "isAuthor": 1,           // 是否是作者 针对二三级评论，即2、3级评论是否与1级评论为同一个user// 1是，默认为0，不是
            "like": 1,               // 当前用户是否点赞过该评论 0：未点赞， 1：点过赞 
            "commentCount": 1       // 一级评论下二、三级评论总数,其余情况为默认值0
        },
	]
}
```



### 3.2.2 查看帖子评论总数: /commentnum

request
```shell
{
    "type": "0",        // 0 股吧评论	1 发一发评论
    "resourceId": ""   // type 为0时为 市场代码_股票代码， type为1时为 msgId 
}
```

response

```json
{
   "total": 1     // 帖子的总评论数，包含一、二、三级评论
}
```



### 3.2.3 我的：查我的评论: /commentone

request

```shell
{
    "msgId": 1,
    "commentId": 1 
}
```

response

```shell
{
    "type": "0",			// 0 股吧评论	1 发一发评论
    "content": "",			// 评论内容 //[300]
    "msgId": 1,				// 被评论的帖子 ID	//long
    "replyCommentId": 1,	// 被评论的原评论 ID  //long  
    "userId": 1,			// 评论者 ID
    "likeCount": 1,			// 点赞数
    "replyTime": "2021-08-02 00:00:00",	// 评论时间
    "commentId": 1          // 评论 ID
}
```



## 3.3 查人

### 3.3.1 查 feed 基本信息： /info

request

无



response

```json
{
    "msgCount": 1,		// 帖子数
    "fansCount": 2,  	// 粉丝数
    "followCount": 3, 	// 关注数
    "collectCount": 4, 	// 收藏数
    "likeCount": 5		// 点赞数
}
```



### 3.3.2 [cancel] 分页查我关注的人: /followuserlist

request

```json
{
    "type": "0",		// 0 我关注的人 1 关注我的人（我的粉丝）
    "pageNum": 1,		// 第几页
    "pageSize": 10		// 每页记录条数
}
```



response

```json
{
    "total": 0,
    "data":
    [
        {
            "userId": 10,
            "isPeerCare": 1	// 是否相互关注，1 是相互关注，0 不是
        },
        {
            "userId": 20,
            "isPeerCare": 0	// 是否相互关注，1 是相互关注，0 不是
        }
	]
}
```



### 3.3.3 [cancel] 分页查我的粉丝：/fans

同 分页查我关注的人



## 3.4 牛牛圈: 分页查排行榜 /ggc/top

request

```shell
{
    "pageNum": 1,		// 第几页
    "pageSize": 10,		// 每页记录条数    
    "type": 1		// 1 等级日榜，2等级周榜，3等级月榜，4等级总榜，5积分日榜，6积分周榜，7积分月榜，7积分总榜
}
```



response

```shell
{
    "total": 0,
    "head": {    	
    	"position": 10	// 第几名， 0 未上榜
    }
    "data":
    [
        {            
            "userId": 2,	// 用户 ID
            "userName": "one", // 用户名
            "value": 100	// 积分值            
        },
        {            
            "userId": 3,	// 用户 ID
            "userName": "two", // 用户名
            "value": 99	// 积分值            
        }
	]
}
```



## 3.5 牛牛圈: 查进击的韭菜天团: /ggc/best

request

无



response

```shell
{
	"total": 0,
	"data": {
		"ArticleList": [{		// 文章列表
			"Abstract": "",		// 文章摘要
			"Author": {			// 作者
				"AuthorAvatarUrl": "",	// 作者头像 url
				"AuthorName": "",	// 作者名
				"AuthorTags": ["authortag"]	// 作者标签
			},
			"CommentCount": 0,	// 评论条数
			"Content": "",		// 文章内容，视频则为视频链接
			"ContentCntw": 0,	// 文章字数
			"CoverImages": [{	// 封面图列表
				"Height": 128,	// 高
				"Width": 256,	// 宽
				"Url": "http://tiktok.com/some.png"	// 原始图片url
			}],
			"DiggCount": 0,			// 点赞数量
			"Extra": "msgId=99",	// 扩展信息 消息id
			"GenerateType": "", // 文章生成类型，渠道
			"GroupId": "",		// 文章组ID
			"GroupImages": [{	// 文章图片列表
				"Height": 11,
				"Width": 22,
				"Url": "http://tiktok.com/some.png"	// 原始图片url
			}],
			"GroupSource": "",
			"GroupType": "",	// 文章类型
			"GroupVideos": [{	// 文章视频列表
				"CoverImage": {	// 封面图
					"Height": 1,
					"Width": 2,
					"Url": "http://tiktok.com/some.png"	// 原始图片url
				},
				"Duration": 100.0,	// 视频时长
				"Id": "2",	// 视频id
				"Url": "http://tiktok.com/123",	// 原始视频url
				"VideoSize": 30000	// 视频文件大小
			}],
			"Link": "",				// 文章原始链接
			"OpenId": "1234",		// 用户id
			"PublishTime": 1629274516,	// 文章发布时间 秒
			"SourceName": "",			// 文章来源名称
			"Tag": ["study"],	// 文章标签，用户自定义标签
			"Tags": ["boy"],	// 文章标签，用户自定义标签
			"Title": ""			// 文章标题
		}],
		"RequestId": "123"	// 请求ID
	}
}
```



## 3.6 牛牛圈: 查推荐：/recommend

request

无



response

```shell
{
	"total": 0,
	"data": {
		"ArticleList": [{		// 文章列表
			"Abstract": "",		// 文章摘要
			"Author": {			// 作者
				"AuthorAvatarUrl": "",	// 作者头像 url
				"AuthorName": "",	// 作者名
				"AuthorTags": ["authortag"]	// 作者标签
			},
			"CommentCount": 0,	// 评论条数
			"Content": "",		// 文章内容，视频则为视频链接
			"ContentCntw": 0,	// 文章字数
			"CoverImages": [{	// 封面图列表
				"Height": 128,	// 高
				"Width": 256,	// 宽
				"Url": "http://tiktok.com/some.png"	// 原始图片url
			}],
			"DiggCount": 0,			// 点赞数量
			"Extra": "msgId=99",	// 扩展信息 消息id
			"GenerateType": "", // 文章生成类型，渠道
			"GroupId": "",		// 文章组ID
			"GroupImages": [{	// 文章图片列表
				"Height": 11,
				"Width": 22,
				"Url": "http://tiktok.com/some.png"	// 原始图片url
			}],
			"GroupSource": "",
			"GroupType": "",	// 文章类型
			"GroupVideos": [{	// 文章视频列表
				"CoverImage": {	// 封面图
					"Height": 1,
					"Width": 2,
					"Url": "http://tiktok.com/some.png"	// 原始图片url
				},
				"Duration": 100.0,	// 视频时长
				"Id": "2",	// 视频id
				"Url": "http://tiktok.com/123",	// 原始视频url
				"VideoSize": 30000	// 视频文件大小
			}],
			"Link": "",				// 文章原始链接
			"OpenId": "1234",		// 用户id
			"PublishTime": 1629274516,	// 文章发布时间 秒
			"SourceName": "",			// 文章来源名称
			"Tag": ["study"],	// 文章标签，用户自定义标签
			"Tags": ["boy"],	// 文章标签，用户自定义标签
			"Title": ""			// 文章标题
		}],
		"RequestId": "123"	// 请求ID
	}
}
```



## 3.7 牛牛圈: 查关注: /careabout

request

无



response

```shell
{
	"total": 0,
	"data": {
		"ArticleList": [{		// 文章列表
			"Abstract": "",		// 文章摘要
			"Author": {			// 作者
				"AuthorAvatarUrl": "",	// 作者头像 url
				"AuthorName": "",	// 作者名
				"AuthorTags": ["authortag"]	// 作者标签
			},
			"CommentCount": 0,	// 评论条数
			"Content": "",		// 文章内容，视频则为视频链接
			"ContentCntw": 0,	// 文章字数
			"CoverImages": [{	// 封面图列表
				"Height": 128,	// 高
				"Width": 256,	// 宽
				"Url": "http://tiktok.com/some.png"	// 原始图片url
			}],
			"DiggCount": 0,			// 点赞数量
			"Extra": "msgId=99",	// 扩展信息 消息id
			"GenerateType": "", // 文章生成类型，渠道
			"GroupId": "",		// 文章组ID
			"GroupImages": [{	// 文章图片列表
				"Height": 11,
				"Width": 22,
				"Url": "http://tiktok.com/some.png"	// 原始图片url
			}],
			"GroupSource": "",
			"GroupType": "",	// 文章类型
			"GroupVideos": [{	// 文章视频列表
				"CoverImage": {	// 封面图
					"Height": 1,
					"Width": 2,
					"Url": "http://tiktok.com/some.png"	// 原始图片url
				},
				"Duration": 100.0,	// 视频时长
				"Id": "2",	// 视频id
				"Url": "http://tiktok.com/123",	// 原始视频url
				"VideoSize": 30000	// 视频文件大小
			}],
			"Link": "",				// 文章原始链接
			"OpenId": "1234",		// 用户id
			"PublishTime": 1629274516,	// 文章发布时间 秒
			"SourceName": "",			// 文章来源名称
			"Tag": ["study"],	// 文章标签，用户自定义标签
			"Tags": ["boy"],	// 文章标签，用户自定义标签
			"Title": ""			// 文章标题
		}],
		"RequestId": "123"	// 请求ID
	}
}
```



## 3.8 [废弃]我的：分页查发布：/mypub

request

```shell
{
    "pageNum": 1,		// 第几页
    "pageSize": 10		// 每页记录条数
}
```


response

```shell
{
	"total": 0,
	"data": {
		"ArticleList": [{		// 文章列表
			"Abstract": "",		// 文章摘要
			"Author": {			// 作者
				"AuthorAvatarUrl": "",	// 作者头像 url
				"AuthorName": "",	// 作者名
				"AuthorTags": ["authortag"]	// 作者标签
			},
			"CommentCount": 0,	// 评论条数
			"Content": "",		// 文章内容，视频则为视频链接
			"ContentCntw": 0,	// 文章字数
			"CoverImages": [{	// 封面图列表
				"Height": 128,	// 高
				"Width": 256,	// 宽
				"Url": "http://tiktok.com/some.png"	// 原始图片url
			}],
			"DiggCount": 0,
			"Extra": "msgId=99",	// 扩展信息 消息id
			"GenerateType": "", // 文章生成类型，渠道
			"GroupId": "",		// 文章组ID
			"GroupImages": [{	// 文章图片列表
				"Height": 11,
				"Width": 22,
				"Url": "http://tiktok.com/some.png"	// 原始图片url
			}],
			"GroupSource": "",
			"GroupType": "",	// 文章类型
			"GroupVideos": [{	// 文章视频列表
				"CoverImage": {	// 封面图
					"Height": 1,
					"Width": 2,
					"Url": "http://tiktok.com/some.png"	// 原始图片url
				},
				"Duration": 100.0,	// 视频时长
				"Id": "2",	// 视频id
				"Url": "http://tiktok.com/123",	// 原始视频url
				"VideoSize": 30000	// 视频文件大小
			}],
			"Link": "",				// 文章原始链接
			"OpenId": "1234",		// 用户id
			"PublishTime": 1629274516,	// 文章发布时间 秒
			"SourceName": "",			// 文章来源名称
			"Tag": ["study"],	// 文章标签，用户自定义标签
			"Tags": ["boy"],	// 文章标签，用户自定义标签
			"Title": ""			// 文章标题
		}],
		"RequestId": "123"	// 请求ID
	}
}


```

## 3.9：/get_article_list 查各种信息流

request

```shell
{
    "type": “short_vedio”,		// short_vedio首页小视频，nnq_video牛牛圈视频，nnq_article牛牛圈图文， finance_article推荐的财经图文
    "ip":"111.111.111.111",
    "province": "省份",
    "refreshType":"" //刷新类型 open 第一次打开app ，refresh 刷新  ,loadmore加载更多
}
```


response

```shell
{
	
	"data": {
		"articlelist": [{		// 文章列表
        	"hl":false,			//是否是华林用户的作品
			"abstract": "",		// 文章摘要
			"author": {			// 作者
				"authoravatarurl": "",	// 作者头像 url
				"authorname": "",	// 作者名
				"authortags": ["authortag"]	// 作者标签
			},
			"commentcount": 0,	// 评论条数
			"content": "",		// 文章内容，视频则为视频链接
			"contentcntw": 0,	// 文章字数
			"coverimages": [{	// 封面图列表
				"height": 128,	// 高
				"width": 256,	// 宽
				"url": "http://tiktok.com/some.png"	// 原始图片url
			}],
			"diggcount": 0,
			"extra": "msgid=99",	// 扩展信息 消息id
			"generatetype": "", // 文章生成类型，渠道
			"groupid": "",		// 文章组id
			"groupimages": [{	// 文章图片列表
				"height": 11,
				"width": 22,
				"url": "http://tiktok.com/some.png"	// 原始图片url
			}],
			"groupsource": "",
			"grouptype": "",	// 文章类型
			"groupvideos": [{	// 文章视频列表
				"coverimage": {	// 封面图
					"height": 1,
					"width": 2,
					"url": "http://tiktok.com/some.png"	// 原始图片url
				},
				"duration": 100.0,	// 视频时长
				"id": "2",	// 视频id
				"url": "http://tiktok.com/123",	// 原始视频url
				"videosize": 30000	// 视频文件大小
			}],
			"link": "",				// 文章原始链接
			"openid": "1234",		// 用户id
			"publishtime": 1629274516,	// 文章发布时间 秒
			"sourcename": "",			// 文章来源名称
			"tag": ["study"],	// 文章标签，用户自定义标签
			"tags": ["boy"],	// 文章标签，用户自定义标签
			"title": ""			// 文章标题
		}],
		"requestid": "123"	// 请求id
	}
}



```



