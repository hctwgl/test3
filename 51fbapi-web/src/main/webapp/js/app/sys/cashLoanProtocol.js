/*
* @Author: nizhiwei
* @Date:   2017-04-06
* @Last Modified by:   Yangyang
* @Last Modified time: 2017-04-07 19:51:09
* @title:  借钱协议
*/


// 利率截取最后两位小数
new Vue({
    el:'.rate1',
    methods:{
        change: function(num){
            var rate=num;
            rate=(rate*100).toFixed(4);   //截取小数点后4位
            rate=parseFloat(rate);
            rate=rate+'%';
            return rate
        }
    }
});

new Vue({
    el:'.rate2',
    methods:{
        change: function(num){
            var rate=num;
            rate=(rate*100).toFixed(4);   //截取小数点后4位
            rate=parseFloat(rate);
            rate=rate+'%';
            return rate
        }
    }
});

new Vue({
    el:'.rate3',
    methods:{
        change: function(num){
            var rate=num;
            rate=(rate*100).toFixed(4);  //截取小数点后4位
            rate=parseFloat(rate);
            rate=rate+'%';
            return rate
        }
    }
});

// 本金金额保留二位小数
new Vue({
    el:'.principal',
    methods:{
        change: function(principalNum){

            principalNum = parseFloat(principalNum);
            principalNum = principalNum.toFixed(2);     //截取小数点后2位
            return principalNum
        }
    }
});