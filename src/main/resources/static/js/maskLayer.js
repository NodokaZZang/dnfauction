function WrapMaskLayer(){
    //화면의 높이와 너비를 구한다.
    var maskHeight = $(document).height();
    var maskWidth = $(window).width();

    //마스크의 높이와 너비를 화면 것으로 만들어 전체 화면을 채운다.
    $('#maskLayer').css({'width':maskWidth,'height':maskHeight});
    $('#maskLayer').show();
    $('#LoadingBar').show();
}

function UnWrapMaskLayer(){
    $('#maskLayer').hide();
    $('#LoadingBar').hide();
}