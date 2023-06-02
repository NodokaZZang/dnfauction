function profileClick() {
    $("#profileInput").click();
}

async function profileChange() {
    let formData = new FormData();

    formData.append("profileImg",document.querySelector("#profileInput").files[0]);

    await fetch("/user/profileChange", {
        method: "POST", // or 'PUT'
        body: formData,
    })
        .then(res => res.json())
        .catch(error => {alert("프로필 변경 실패")})
        .then(res => {if (res.result)
        {
            alert("프로필을 변경하였습니다.");
            GetUserInfo().then((data)=> {

                if (res.result === true)
                {
                    $("#userProfileImg").attr("src", data.data.userProfile);
                    $("#username").text(data.data.username);
                }

            });

        } else {
            alert("프로필 변경 실패");
        } });
}

function CreateMyBoardTable(list)
{
    let strTable = "";
    $("#auctionMyInfoBoard").empty();
    list.forEach((item)=> {
        strTable +=
            `<tr style="cursor: pointer;" onclick="BoardDetail(${item.sq})">
            <td>${item.sq}</td>
            <td>${item.title}</td>
            <td>${item.regBy}</td>
            <td>${item.regDt}</td>
            <td>${item.view}</td>
            <td>${item.vote}</td>
        </tr>`;
    })
    $("#auctionMyInfoBoard").append(strTable);
}

let myPage = 0;
async function ListViewMyBoard(page)
{
    var obj = {} ;
    myPage = page;
    obj.nowPage = page;

    const response = await PostJSON("/board/mylistView",obj);

    if (response.result)
    {
        let listCount = response.listCount;
        CreateMyBoardTable(response.list);
        MyPageRender(page,listCount);
    }
}


function MyPageRender(nowPage, endPage)
{
    $("#auctionMyInfoBoardPage").empty();

    let firstNumber = Math.floor(nowPage/10) * 10;
    let lastNumber = Math.min(endPage,(firstNumber + 10) - 1);

    // 1 2 3 4 5 6 7 8 9 10
    let pageStr = `<nav aria-label="Page navigation example">
                        <ul class="pagination">`;

    if (nowPage !== 0)
    {
        pageStr += `
        <li class="page-item" onclick="mypageMove(-1)">
            <a class="page-link" href="#" aria-label="Previous">
                                    <span aria-hidden="true">&laquo;</span>
                                </a>
                            </li>`
    }

    for (let i = firstNumber; i <= lastNumber; i++)
    {
        if (i === nowPage)
            pageStr += `<li class="page-item active"><a class="page-link" data-page="${i}" href="#">${i+1}</a></li>`
        else
            pageStr += `<li class="page-item" onclick="mypageMove(${i})"><a class="page-link" data-page="${i}" href="#">${i+1}</a></li>`
    }

    if (nowPage !== endPage)
    {
        pageStr += `
                              <li class="page-item" onclick="mypageMove(-2)">
                                <a class="page-link" href="#" aria-label="Next">
                                    <span aria-hidden="true">&raquo;</span>
                                </a>
                            </li>`
    }

    pageStr += `  </ul>
                    </nav>`

    $("#auctionMyInfoBoardPage").append(pageStr);
}

function mypageMove(item)
{
    if (item == -1) // 왼쪽으로 이동
    {
        myPage -= 1;
        ListViewMyBoard(myPage);
    }
    else if (item == -2) // 오른쪽으로 이동
    {
        myPage += 1;
        ListViewMyBoard(myPage);
    }
    else
    {
        ListViewMyBoard(item);
    }
}

async function ExcelDownload()
{
    WrapMaskLayer();
    const response = await fetch('/download');
    const file = await response.blob();
    const downloadUrl = window.URL.createObjectURL(file); // 해당 file을 가리키는 url 생성

    const anchorElement = document.createElement('a');
    document.body.appendChild(anchorElement);
    anchorElement.download = '작성게시글목록'; // a tag에 download 속성을 줘서 클릭할 때 다운로드가 일어날 수 있도록 하기
    anchorElement.href = downloadUrl; // href에 url 달아주기

    anchorElement.click(); // 코드 상으로 클릭을 해줘서 다운로드를 트리거

    document.body.removeChild(anchorElement); // cleanup - 쓰임을 다한 a 태그 삭제
    UnWrapMaskLayer();
}