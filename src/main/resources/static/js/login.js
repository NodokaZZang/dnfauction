const LOGIN_PAGE = 0;
const SIGN_UP_PAGE = 1;
function PageChange(PAGE)
{
    switch (PAGE)
    {
        case LOGIN_PAGE:
            LoginPageChange();
            break;

        case SIGN_UP_PAGE:
            SignUpPageChange();
            break;
    }
}
function LoginPageChange()
{
    $("#loginBox").show();
    $("#signUpBox").hide();

    $("#useremail").val("");
    $("#userPw").val("");
}

function SignUpPageChange()
{
    $("#loginBox").hide();
    $("#signUpBox").show();

    $("#s_useremail").val("");
    $("#s_userPw").val("");
    $("#s_code").val("");
}

async function EmailCheckSend()
{
    var obj = {};
    obj.useremail = $("#s_useremail").val();

    let regex = new RegExp('[a-z0-9]+@[a-z]+\.[a-z]{2,3}');
    if (regex.test(obj.useremail) == false) { alert("유효하지 않은 이메일 형식입니다");  return;} ;

    WrapMaskLayer();

    const response = await PostJSON("/user/emailCheckSend", obj);

    if (response.result == true)
    {
        alert("이메일로 인증코드를 발송하였습니다");
        $("#uuid").val(response.authKey);
    }
    else
    {
        alert("이메일 인증코드 전송 실패");
    }

    UnWrapMaskLayer();
}

async function SignUp()
{
    if ($("#uuid").val().trim() === "" || $("#uuid").val() === undefined || $("#uuid").val() === null)
    {
        alert("이메일 인증번호를 요청하세요");
        return;
    }

    else if ($("#s_useremail").val().trim() === "" || $("#s_useremail").val() === undefined || $("#s_useremail").val() === null)
    {
        alert("이메일을 입력하세요");
        return;
    }

    else if ($("#s_userPw").val().trim() === "" || $("#s_userPw").val() === undefined || $("#s_userPw").val() === null)
    {
        alert("패스워드를 입력하세요");
        return;
    }

    var obj = {};
    obj.useremail = $("#s_useremail").val().trim();
    obj.userPw = $("#s_userPw").val().trim();
    obj.uuid = $("#uuid").val().trim();

    WrapMaskLayer();

    const response = await PostJSON("../user/signUp", obj);

    if (response.result == true)
    {
        alert("회원가입 성공 다시 로그인 해주세요");
        $("#uuid").val(response.authKey);
        PageChange(LOGIN_PAGE);
    }
    else
    {
        alert(response.message);
    }

    UnWrapMaskLayer();
}

