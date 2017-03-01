/*
* @Author: Yangyang
* @Date:   2017-02-14 16:52:58
* @Last Modified by:   Yangyang
* @Last Modified time: 2017-03-01 21:26:59
* @title:  领取优惠券
*/


// 领取优惠劵
$(function(){
	$(".receiveCoupons_btn").click(function(event) {
		$(this).parent().addClass("receiveCoupons_alreadyReceive");
		$(this).attr("disabled","true");
	});
});

// $(function(){

//     var couponList = $("#couponList").val();
//     console.log(couponList);

//     // var returnJson= JSON.parse(couponList);
//     // console.log(returnJson);

//     // var returnJson= couponList.toJSONString();
//     // console.log(returnJson);


//     var returnJson= eval('(' + couponList + ')');



    
//     // var returnJson= noescape(couponList);
    

//     // var returnJson= JSON.stringify(couponList);
//     // var returnJson1= JSON.parse(returnJson);
    
//     // var returnJson=$number.format("#0", $couponList)
    



//     console.log(returnJson);





//     var length=returnJson.length;
//     console.log(length);

//     for (var i = 0; i < returnJson.length; i++) {

//         var amountNum = returnJson[i].amount;
//         console.log(amountNum);

//         // var index = 2.58;
//         // var j=Math.round(amount);
        
//         var j=parseInt(amountNum);
//         $(".box1").eq(i).text(j);
       
//     }

// });
