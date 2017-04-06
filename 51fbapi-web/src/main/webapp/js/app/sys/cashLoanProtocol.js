/*
 * @Author: nizhiwei
 * @Date:   2017-04-06
 * @Last Modified by:   nizhiwei
 * @Last Modified time: 2017-04-06
 * @title:  借钱协议
 */
new Vue({
    el:'.cashLoanProtocol',
    methods:{
        change: function(num){
            var rate=num;
            rate=(rate*100).toFixed(4);       //截取小数点后4位
            rate=parseFloat(rate);
            rate=rate+'%';
            return rate
        }
    }
});