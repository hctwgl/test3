/*
* @Author: Yangyang
* @Date:   2017-02-15 16:08:03
* @Last Modified by:   Yangyang
* @Last Modified time: 2017-02-17 09:53:42
* @title:  商品列表页
*/


// 顶部导航tab栏切换





















































// 点击tab切换
$(".goodsList_header li").click(function(event) {
    $(this).addClass('current').siblings().removeClass('current');
    var i=$(this).index();
    $(".mui-content").eq(i).removeClass("goodsList_main_hide").siblings().addClass("goodsList_main_hide");

});

// 评价下拉加载刷新
$(function(){
    mui.init({
		pullRefresh: {
			container: '#pullrefresh',
			down : {
				contentrefresh: '下拉加载...',
				callback: pulldownRefresh,
				auto:false						
			},
			up : {
				contentrefresh: '正在加载...',
				callback: pullupRefresh,
				auto:false
			}
		}
	});

	// 下拉刷新不做任何操作
	function pulldownRefresh() {
		mui('#pullrefresh').pullRefresh().endPulldownToRefresh(true); //参数为true代表没有更多数据了。
	}

	var pageNo = 1;
	var objString = $("#temMessageList").val();
	$(function(){
		var messageList = eval("(" + objString + ")"); 
		if(messageList!=undefined){
			pageNo = 2;
		}		
	}); 
        
	var pageCount=20;//每页多少条数据
	//上拉加载具体业务实现
	function pullupRefresh() {
		setTimeout(function() {
			var isOn=true;
	        $.ajax({
    			url: '/h5/wx/mine/mineMoneyDetailDate',
    			type: 'POST',
    			dataType: 'JSON',
    			data: {
    				type : "R",
    				mobile: mobileNum
    			},
    			success: function(returnData){
    				if (returnData.success) {
    					var messageData = returnJson.data;
	      	            if (messageData.length >0) {
	      	            	var messageList =messageData.messageList;
		      	            var currentPage =messageData.currentPage;                         
		      	            var table = document.body.querySelector('.mui-table-view');
		                    var cells = document.body.querySelectorAll('.thelist_li');
		                    var messageCount=messageList.length;
							for (var i = cells.length, len = i + messageCount; i < len; i++) {
				                var messageInfo=messageList[i-cells.length];
				                var messageDes="";
				                if(messageInfo.sourceType==1){
				                    messageDes = "您获得一个"+messageInfo.money+"元";
				                }else if(messageInfo.sourceType==2&&messageInfo.level == 1){
				                  	messageDes = "您邀请的用户"+messageInfo.orderUser+"消费"+messageInfo.sourceAmount+"元给您带来收益";
				                }else if(messageInfo.sourceType==2&&messageInfo.level == 2){
				                  	messageDes = "您邀请的用户"+messageInfo.orderUser+"邀请了"+messageInfo.midUser+"消费"+messageInfo.sourceAmount+"元给您带来收益";
				                }else{
				                    messageDes = "系统到账"+messageInfo.money+"元";
				                }
				                var li = document.createElement('li');
					            li.className = 'thelist_li';
					            li.innerHTML = '<p class="titleP">'+messageDes+'</p><p class="timeP">' + getLocalTime(messageInfo.redPacketTime)+ '</p><p class="moneyP">'+messageInfo.money+'元</p>';
					            table.appendChild(li);
				            }
			           
				            if(cells.length+messageCount<20*currentPage){
					            isOn= true;
				            }else{
					            isOn= false;
							}
				            if(!isOn){
					            pageNo+=1;
							}
						}
    				} else {
    					requestMsg(returnData.msg);
    				}
    				mui('#pullrefresh').pullRefresh().endPullupToRefresh(isOn); //参数为true代表没有更多数据了。
    			},
    			error: function(){
		      		requestMsg("请求失败");
    			}
    		})
		}, 1500);
	}
});








