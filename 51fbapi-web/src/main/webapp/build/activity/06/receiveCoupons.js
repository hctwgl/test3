//点击我要领券时判断用户是否登录 此时要先判断系统 iPhone和andriod 
// getBlatFrom() == 2代表iOS
//（参考motherDay点击抢优惠券function）
let userName = "";
if(getInfo().userName){
    userName=getInfo().userName;
};
$(function(){

	function createCoupon(data,imgUrl){
		//if (num=0) {}
		    let url="https://fs.51fanbei.com/h5/app/activity/06/"+imgUrl;
			let cont=`<li class="couponLi">
		    <p class="positionYuan positionYuan01"></p>
            <p class="positionYuan positionYuan02"></p>
			<div class="couponKind"><img src="${url}"></div>
			<div class="couponTop">
				<p class="cashCoupon">${data.name}</p>
				<p class="timeOver">(即将过期)</p>
				<p class="couponSize">￥<span>20</span></p>
				<p class="expiryDate">有效期2017-08-08</p>
				<p class="fullCut">满100元可用</p>
			</div>
            <p class="clickCoupon">点击领券</p>
		</li>`;
		$('.couponList').append(cont);
	}

	//ajax请求获取数据进行填充
	let cont1={
		name:'满减券'

	};
	let cont2={
		name:"还款抵用券"
	}
    /*createCoupon(cont1,'getCoupon02.png')
    createCoupon(cont2,'getCoupon02.png')*/
    //请求优惠券列表数据
    $.ajax({
    	url:'/fanbei-web/receiveCoupons',
    	data:{'userName':userName},
    	type:'get',
    	success:function(data){
    		console.log(data)
    		/*createCoupon(data,'getCoupon01.png')
    		createCoupon(data,'getCoupon02.png')*/
    	}
    })
})
