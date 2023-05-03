const isMobile = () => {
    const user = navigator.userAgent;
    let isCheck = false;

    if ( user.indexOf("iPhone") > -1 || user.indexOf("Android") > -1 ) {
        isCheck = true;
    }

    return isCheck;
}

const screenList = ["dynamicSearch", "auctionSearch", "auctionItemSearch"];

function NavSelect(id) {
    for (var i = 0; i < $(".custom-nav > a").length; i++) {
        const navItem = $($(".custom-nav > a")[i]);
        navItem.removeClass("active");
    }
    $(id).addClass("active");
    let screenIndex = Number($(id).attr("data-screen"));

    for (let i = 0; i < screenList.length; i++) {
        $("#"+screenList[i]).hide();
    }

    $("#" + screenList[screenIndex]).show();

    SearchAuctionSearch();

    $("#searchOpt").val(screenIndex);
    if (isMobile())
        $("#menuButton").click();
}

function NavSelectIndex(index) {
    for (var i = 0; i < $(".custom-nav > a").length; i++) {
        const navItem = $($(".custom-nav > a")[i]);
        navItem.removeClass("active");
    }
    $($(".custom-nav > a")[index]).addClass("active");

    let screenIndex= Number($($(".custom-nav > a")[index]).attr("data-screen"));

    for (let i = 0; i < screenList.length; i++) {
        $("#"+screenList[i]).hide();
    }

    $("#" + screenList[screenIndex]).show();
}