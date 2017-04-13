/*
* @Author: nizhiwei
* @Date:   2017-04-06
* @Last Modified by:   Yangyang
* @Last Modified time: 2017-04-13 13:32:43
* @title:  借钱协议
*/


new Vue({
    el:'.cashLoanProtocol',
    methods:{
        changeRate: function(num){ // 利率截取最后两位小数
            var rate=num;
            rate = (rate*100).toFixed(4);   //截取小数点后4位
            rate = parseFloat(rate);
            rate = rate+'%';
            return rate
        },
        changePrincipal: function(principalNum){ // 本金金额保留二位小数

            principalNum = parseFloat(principalNum);
            principalNum = principalNum.toFixed(2);     //截取小数点后2位
            return principalNum
        }
    }
});