// 去洗哟
function downloadUrl(downloadUrl){
    $(".downloadFooter").click(function(){
        if (navigator.userAgent.match(/(iPhone|iPod|iPad);?/i)) {
            window.location.href = "Himalaya://";
                var loadDateTime = new Date();
                window.setTimeout(function () {
                    var timeOutDateTime = new Date();
                    if (timeOutDateTime - loadDateTime < 5000 && location.href.indexOf('Himalaya://') == -1){
                        window.location = downloadUrl;
                    } else {
                        window.close();
                    };
                },
                2500);
        } else if (navigator.userAgent.match(/android/i)) {
            var state = null;
            try {
                state = window.open("himalaya://home", '_blank');
            } catch(e) {}
            if (state) {
                window.close();
            } else {
                window.location = downloadUrl;
            };
        };
    });
};
