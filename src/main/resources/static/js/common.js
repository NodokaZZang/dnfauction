async function PostJSON(url, data) {
    try {
        const response = await fetch(url, {
            method: "POST", // or 'PUT'
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data),
        });

        const result = await response.json();
        console.log("Success:", result);
        return result;
    } catch (error) {
        console.error("Error:", error);
        return null;
    }
}

async function GetJSON(url) {
    try {
        const response = await fetch(url, {
            method: "GET", // or 'PUT'
            headers: {
                "Content-Type": "application/json",
            }
        });

        const result = await response.json();
        console.log("Success:", result);
        return result;
    } catch (error) {
        console.error("Error:", error);
        return null;
    }
}

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

async function GetUserInfo() {
    return await GetJSON("/user/userInfo");
}