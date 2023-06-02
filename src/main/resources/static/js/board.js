var Page = 0;
async function RegisterBoard()
{
    const title = $("#boardTitle").val();
    const content = $("#boardContent").val();

    if (title.trim() === "" || title === undefined || title === null)
    {
        alert("제목은 필수 입력값입니다");
        return;
    }

    var obj = {} ;

    obj.title = title;
    obj.content = content;

    const response = await PostJSON("/board/register",obj);
    if (response.result)
    {
        alert("게시글 등록 성공");
        BoardClear();
        $("#moveBoard").click();
    }
    else
    {
        alert("게시글 등록 실패");
    }
}

async function ListViewBoard(page)
{
    var obj = {} ;
    Page = page;
    obj.nowPage = page;

    const response = await PostJSON("/board/listView",obj);

    if (response.result)
    {
        let listCount = response.listCount;
        CreateBoardTable(response.list);
        PageRender(page,listCount);
    }
}

function BoardClear()
{
    $("#boardTitle").val("");
    $(".note-editable").empty();
}

function PageRender(nowPage, endPage)
{
    $("#boardPage").empty();

    let firstNumber = Math.floor(nowPage/10) * 10;
    let lastNumber = Math.min(endPage,(firstNumber + 10) - 1);

    // 1 2 3 4 5 6 7 8 9 10
    let pageStr = `<nav aria-label="Page navigation example">
                        <ul class="pagination">`;

    if (nowPage !== 0)
    {
        pageStr += `
        <li class="page-item" onclick="pageMove(-1)">
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
            pageStr += `<li class="page-item" onclick="pageMove(${i})"><a class="page-link" data-page="${i}" href="#">${i+1}</a></li>`
    }

    if (nowPage !== endPage)
    {
        pageStr += `
                              <li class="page-item" onclick="pageMove(-2)">
                                <a class="page-link" href="#" aria-label="Next">
                                    <span aria-hidden="true">&raquo;</span>
                                </a>
                            </li>`
    }

    pageStr += `  </ul>
                    </nav>`

    $("#boardPage").append(pageStr);
}

function pageMove(item)
{
    if (item == -1) // 왼쪽으로 이동
    {
        Page -= 1;
        ListViewBoard(Page);
    }
    else if (item == -2) // 오른쪽으로 이동
    {
        Page += 1;
        ListViewBoard(Page);
    }
    else
    {
        ListViewBoard(item);
    }
}

function CreateBoardTable(list)
{
    let strTable = "";
    $("#auctionBoardTable").empty();
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
    $("#auctionBoardTable").append(strTable);

}

async function BoardDetail(sq)
{
    var obj = {};
    obj.sq = sq;

    const response = await PostJSON("/board/detail", obj);
    $("#boardDetailToolBar").empty();
    if (response.result)
    {
        $("#regByProfile").attr("src", response.data.regByProfile);
        $("#goBoardDetail").click();
        $("#boardDetailTitle").text(response.data.title);
        $("#boardCanvas").html(response.data.content);
        $("#regBy").text(response.data.regBy);
        $("#regDt").text(response.data.regDt);
        $("#boardViewCount").text("조회수: " +response.data.view);
        $("#boardDetailSq").val(response.data.sq);
        $("#boardDetailVote").text(response.data.vote);

        const res = await GetJSON("/user/userInfo");
        if (res.result)
        {
            if (response.data.regBy === res.data.username)
            {
                $("#boardDetailToolBar").append(`<button class="btn-primary btn" onclick="ModifyBoardPage()" style="margin-right: 10px;">수정</button>
                <button class="btn-danger btn" onclick="DeleteBoard()">삭제</button>`);
            }
        }

        await ListCommentView();

    }
}

async function BoardVote()
{
    var obj = {};
    obj.sq = $("#boardDetailSq").val();

    const response = await PostJSON("/board/vote", obj);

    if (response.result)
    {
        $("#boardDetailVote").text(response.data.vote);
    }
    else
    {
        alert(response.message);
    }
}

async function ModifyBoardPage()
{
    ModifyBoardClear();
    $("#goBoardDetailModify").click();
    $("#boardTitleModify").val($("#boardDetailTitle").text());
    $('#boardContentModify').summernote('code', $("#boardCanvas").html());
    $("#boardmodifySq").val($("#boardDetailSq").val());
}

function ModifyBoardClear()
{
    $("#boardTitleModify").val("");
    $('#boardContentModify').summernote('code', '');
    $("#boardmodifySq").val("");
}

async function ModifyBoard()
{
    const title = $("#boardTitleModify").val();
    const content = $("#boardContentModify").val();

    if (title.trim() === "" || title === undefined || title === null)
    {
        alert("제목은 필수 입력값입니다");
        return;
    }

    var obj = {} ;

    obj.title = title;
    obj.content = content;
    obj.sq = $("#boardmodifySq").val()

    const response = await PostJSON("/board/modify",obj);
    if (response.result)
    {
        alert("게시글 수정 성공");
        ModifyBoardClear();
        $("#moveBoard").click();
    }
    else
    {
        alert("게시글 수정 실패");
    }
}

async function DeleteBoard()
{
    var obj = {} ;

    obj.sq = $("#boardDetailSq").val()
    const response = await PostJSON("/board/delete",obj);

    if (response.result)
    {
        alert("게시글 삭제 성공!");
        BoardClear();
        ModifyBoardClear();
        $("#moveBoard").click();
    }
    else
    {
        alert("게시글 삭제 실패");
    }
}


function CreateComment(list, username,level = 0)
{
    let str = "";

    for (let i = 0; i < list.length; i++)
    {
        const object = list[i];

        if (object.children.length > 0)
        {
            if (object.deleteYn !== null)
            {
                str += `
                            <div class="d-flex MFontRegular">
                            ${replyDeco(level)}
                            <div class="flex-shrink-0"><img style="width: 50px; height: 50px;" class="rounded-circle" src="${object.userProfile}" alt="..."></div>
                            <div class="ms-3" style="width: 100%;">
                                <div class="fw-bold">${object.regBy}                 
                                </div>
                                <div id="commentContent_${object.sq}" style="padding: 5px;">삭제된 댓글 입니다.</div>
                                <div style="display: none" id="commentReplyBox_${object.sq}">
                                    <form class="mb-4" style="display: flex">
                                        <textarea class="form-control" id="commentInput_${object.sq}" rows="3" placeholder="댓글을 입력하세요!"></textarea>
                                        <button class="btn btn-primary" onclick="RegisterCComment(${object.sq})">작성</button>
                                    </form>
                                </div>
                                ${CreateComment(object.children, username,level + 1)} 
                            </div>
                        </div>`
            }
            else
            {
                str += `
                            <div class="d-flex MFontRegular">
                            ${replyDeco(level)}
                            <div class="flex-shrink-0"><img style="width: 50px; height: 50px;" class="rounded-circle" src="${object.userProfile}" alt="..."></div>
                            <div class="ms-3" style="width: 100%;">
                                <div class="fw-bold">${object.regBy}
                                  <span class="commentAction" id="commentReply_${object.sq}" onclick="CcommentOpen(${object.sq})">답글</span> ${UpdateDeleteCommentAuth(username, object.regBy, object.sq)}
                                </div>
                                <div id="commentContent_${object.sq}" style="padding: 5px;">${object.content}</div>
                                <div id="commentModifyBox_${object.sq}" style="width: 100%; display: none">
                                    <form class="mb-4" style="display: flex">
                                        <textarea class="form-control" id="commentUpdate_${object.sq}" rows="3" placeholder="댓글을 입력하세요!"></textarea>
                                        <button class="btn btn-primary" onclick="ModifyCComment(${object.sq})">작성</button>
                                    </form>
                                </div>
                                <div style="display: none" id="commentReplyBox_${object.sq}">
                                    <form class="mb-4" style="display: flex">
                                        <textarea class="form-control" id="commentInput_${object.sq}" rows="3" placeholder="댓글을 입력하세요!"></textarea>
                                        <button class="btn btn-primary" onclick="RegisterCComment(${object.sq})">작성</button>
                                    </form>
                                </div>
                                ${CreateComment(object.children, username,level + 1)} 
                            </div>
                        </div>`
            }
        }
        else
        {
            if (object.deleteYn !== null)
            {
                str += `
                            <div class="d-flex MFontRegular">
                            ${replyDeco(level)}
                            <div class="flex-shrink-0">
                            <img style="width: 50px; height: 50px;" class="rounded-circle" src="${object.userProfile}" alt="...">
                            </div>
                            <div class="ms-3" style="width: 100%;">
                                <div class="fw-bold">
                                ${object.regBy}    
                                </div>
                                <div id="commentContent_${object.sq}"  style="padding:5px;">삭제된 댓글 입니다.</div>
                                <div style="display: none" id="commentReplyBox_${object.sq}">
                                    <form class="mb-4" style="display: flex">
                                        <textarea class="form-control" id="commentInput_${object.sq}" rows="3" placeholder="댓글을 입력하세요!"></textarea>
                                        <button class="btn btn-primary" onclick="RegisterCComment(${object.sq})">작성</button>
                                    </form>
                                </div>
                            </div>
                        </div>`
            }
            else
            {
                str += `
                            <div class="d-flex MFontRegular">
                            ${replyDeco(level)}
                            <div class="flex-shrink-0">
                            <img style="width: 50px; height: 50px;" class="rounded-circle" src="${object.userProfile}" alt="...">
                            </div>
                            <div class="ms-3" style="width: 100%;">
                                <div class="fw-bold">
                                ${object.regBy}  
                                <span class="commentAction" id="commentReply_${object.sq}" onclick="CcommentOpen(${object.sq})">답글</span> ${UpdateDeleteCommentAuth(username, object.regBy, object.sq)}
                                </div>
                                <div id="commentContent_${object.sq}"  style="padding:5px;">${object.content}</div>
                                <div id="commentModifyBox_${object.sq}" style="width: 100%; display: none;">
                                    <form class="mb-4" style="display: flex;">
                                        <textarea class="form-control" id="commentUpdate_${object.sq}" rows="3" placeholder="댓글을 입력하세요!"></textarea>
                                        <button class="btn btn-primary" onclick="ModifyCComment(${object.sq})">작성</button>
                                    </form>
                                </div>
                                <div style="display: none" id="commentReplyBox_${object.sq}">
                                    <form class="mb-4" style="display: flex">
                                        <textarea class="form-control" id="commentInput_${object.sq}" rows="3" placeholder="댓글을 입력하세요!"></textarea>
                                        <button class="btn btn-primary" onclick="RegisterCComment(${object.sq})">작성</button>
                                    </form>
                                </div>
                            </div>
                        </div>`
            }
        }
    }
    return str;
}

function replyDeco(level)
{
    if (level > 0) {return `<img src="/img/reply.png" style="width: 50px;height: 50px;">`}
    else
        return "";
}

function UpdateDeleteCommentAuth(regBy, username, sq)
{
    if (username === regBy)
    {
        return `<span id="commentUpdateCheck_${sq}" class="commentAction" onclick="ModifyCommentOpen(${sq})">수정</span> <span class="commentAction" onclick="CommentDelete(${sq})">삭제</span> `;
    }
    else
    {
        return "";
    }
}

async function RegisterComment()
{
    if ($("#commentInput").val().trim() === "" || $("#commentInput").val() === undefined || $("#commentInput").val() === null)
    {
        alert("내용을 입력하세요");
        return;
    }

    var obj = {};
    obj.content = $("#commentInput").val();
    obj.boardSq = $("#boardDetailSq").val();

    const response = await PostJSON("/board/commentRegister", obj);

    if (response.result === true)
    {
        alert("댓글을 등록하였습니다");
        $("#commentInput").val("");
        ListCommentView();
    }
    else
    {
        alert("댓글 등록 실패!");
    }

}

async function RegisterCComment(parentSq)
{
    if ($(`#commentInput_${parentSq}`).val().trim() === "" || $(`#commentInput_${parentSq}`).val() === undefined || $(`#commentInput_${parentSq}`).val() === null)
    {
        alert("내용을 입력하세요");
        return;
    }

    var obj = {};
    obj.content = $(`#commentInput_${parentSq}`).val();
    obj.boardSq = $("#boardDetailSq").val();
    obj.parentSq = parentSq;

    const response = await PostJSON("/board/ccommentRegister", obj);

    if (response.result === true)
    {
        alert("댓글을 등록하였습니다");
        ListCommentView();
    }
    else
    {
        alert("댓글 등록 실패!");
    }
}



async function ListCommentView()
{
    var obj = {};
    obj.sq = $("#boardDetailSq").val();
    $("#commentRegion").empty();
    const response = await PostJSON("/board/commentList", obj);
    console.log("comment: ", response);

    const userInfo = await GetUserInfo();
    const username = userInfo.data.userId;

    if (response.result)
    {
        $("#commentRegion").append(CreateComment(response.list, username));
    }

}

function CcommentOpen(sq) {

    const text = $("#commentReply_"+sq).text();

    if (text === "답글")
    {
        $(`#commentReply_${sq}`).text("취소");
        $(`#commentReplyBox_${sq}`).show();
    }
    else
    {
        $(`#commentReply_${sq}`).text("답글");
        $(`#commentReplyBox_${sq}`).hide();
    }
}

async function CommentDelete(sq)
{
    if (confirm("정말로 댓글을 삭제하시겠습니까?"))
    {
        var obj ={};
        obj.sq = sq;

        const response = await PostJSON("/board/deleteComment", obj);

        if(response.result)
        {
            ListCommentView();
        }
        else
        {
            alert("삭제 실패");
        }
    }
}

function ModifyCommentOpen(sq)
{
    const state = $(`#commentUpdateCheck_${sq}`).text();
    
    if (state === "수정")
    {
        $(`#commentUpdateCheck_${sq}`).text("수정 취소");

        const text = $(`#commentContent_${sq}`).text();
        $(`#commentContent_${sq}`).hide();
        $(`#commentUpdate_${sq}`).val(text);
        $(`#commentModifyBox_${sq}`).show();
    }
    else
    {
        $(`#commentUpdateCheck_${sq}`).text("수정");
        $(`#commentContent_${sq}`).show();
        $(`#commentModifyBox_${sq}`).hide();
        $(`#commentUpdate_${sq}`).val("");
    }
}

async function ModifyCComment(sq)
{
    if ($(`#commentUpdate_${sq}`).val().trim() === "" || $(`#commentUpdate_${sq}`).val() === undefined || $(`#commentUpdate_${sq}`).val() === null)
    {
        alert("내용을 입력하세요");
        return;
    }

    var obj = {};
    obj.content = $(`#commentUpdate_${sq}`).val();
    obj.commentSq = sq;

    const response = await PostJSON("/board/ModifyCComment", obj);

    if (response.result === true)
    {
        alert("댓글을 수정하였습니다");
        ListCommentView();
    }
    else
    {
        alert("댓글 수정 실패!");
    }
}