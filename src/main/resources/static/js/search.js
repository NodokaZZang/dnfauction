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

function DoSearch () {
    const val = $("#searchOpt").val();

    switch (val) {
        case '1':
            SearchAuction();
            break;
        case '2':
            SearchAuctionItem();
            break;
    }
}


var auctionChart = null;
var VAuctionDataList = null;
var VAuctionItemDataList = null;
var auctionSearchChart = null;
var VAuctionSearchDataList = null;
async function SearchAuction() {

    const value = $("#searchText").val().trim();

    if (value === "" || value === undefined || value === null)
        return;

    WrapMaskLayer();

    var obj = {};
    obj.itemName = value;

    const response = await PostJSON("../api/AuctionSold",obj);
    console.log(response);
    UnWrapMaskLayer();

    if (response.result === false)
        return;

    NavSelectIndex(1);

    VAuctionDataList = response.data.rows;

    VActionDataListRender();

    const chartDataList = response.auctionDataList;

    if (chartDataList.length > 0) {

        const dataSetList =[];
        const dataLabelList =[];

        const upperChartData = {};
        upperChartData.label = chartDataList[0].itemName + " 하한가";
        upperChartData.borderColor = "rgb(54, 162, 235)"
        upperChartData.backgroundColor = 'rgba(54, 162, 235, 0.5)'
        upperChartData.borderWidth = 1;
        upperChartData.data = [];

        const lowerChartData = {};
        lowerChartData.label = chartDataList[0].itemName + " 상한가";
        lowerChartData.borderColor = "rgb(255,81,81)"
        lowerChartData.backgroundColor = 'rgba(255,42,95,0.5)'
        lowerChartData.borderWidth = 1;
        lowerChartData.data = [];


        chartDataList.forEach((charData) => {
            upperChartData.data.unshift(charData.unitPriceL);
            lowerChartData.data.unshift(charData.unitPrice);
            dataLabelList.unshift(charData.soldDate);
        });

        dataSetList.push(upperChartData);
        dataSetList.push(lowerChartData);

        const data = {};
        data.datasets = dataSetList;
        data.labels = dataLabelList;
        const config = {
            type: 'line',
            data: data,
            options: {
                type: 'myScale',
                responsive: true,
                plugins: {
                    legend: {
                        position: 'top',
                    },
                    title: {
                        display: true,
                        text: chartDataList[0].itemName + " 상한가 추이"
                    }
                }
            },
        };

        if (auctionChart !== null)
            auctionChart.destroy();

        auctionChart = document.getElementById('auctionChart');

        auctionChart = new Chart(auctionChart, config);
    }
}
async function SearchAuctionItem() {
    const value = $("#searchText").val().trim();

    if (value === "" || value === undefined || value === null)
        return;

    WrapMaskLayer();

    var obj = {};
    obj.itemName = value;

    const response = await PostJSON("../api/AuctionItem",obj);
    console.log(response);
    UnWrapMaskLayer();

    if (response.result === false)
        return;

    NavSelectIndex(2);

    VAuctionItemDataList = response.data.rows;

    VActionItemDataListRender();
}
function resizeChart() {
    if (auctionChart) auctionChart.resize();
}
function RefineRender(value) {
    return `<p style='color: yellow'>+${value}</p>`
}
function ReinforceRender(value) {
    return `<p style='color: dodgerblue'>+${value}</p>`
}
function AmountRender(value) {
    return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}
function ItemRarityRender(value) {
    switch (value) {
        case "신화":
            return"color: #ff0000;"

        case "에픽":
            return "color: #ffb412";

        case "레전더리":
            return "color: #ff7800;"

        case "레어":
            return "color: #8a2be2";

        case "유니크":
            return "color: #ff1864;"

        case "크로니클":
            return  "color: #cd5c5c;"

        case "언커먼":
           return "color: #00ffdc;";

        case "커먼":
            return "color: #ffff;";
    }
}
function UpgradeRender(value) {
    if (value === undefined || value === null)
        return "";

    else
        return " +" + value;
}
function VActionDataListRender() {
    $("#dynamicAuctionTable").empty();

    let tableStr = "";
    VAuctionDataList.forEach((item,index) => {
        tableStr +=
            `<tr>
                <td><img src="https://img-api.neople.co.kr/df/items/${item.itemId}" style="width: 50px; height: 50px;"></td>
                <td>${ReinforceRender(item.reinforce)}</td>
                <td>${RefineRender(item.refine)}</td>
                <td style="${ItemRarityRender(item.itemRarity)}">${item.itemName} ${UpgradeRender(item.upgrade)}</td>
                <td>${item.itemAvailableLevel}</td>
                <td>${item.soldDate}</td>
                <td>${AmountRender(item.unitPrice)}</td>
            </tr>`;
    });

    $("#dynamicAuctionTable").append(tableStr);
}
function VAuctionDataListCostASC() {
    VAuctionDataList = VAuctionDataList.sort((left, right) => left.unitPrice - right.unitPrice);
    VActionDataListRender();
}
function VAuctionDataListCostDESC() {
    VAuctionDataList = VAuctionDataList.sort((left, right) => right.unitPrice - left.unitPrice);
    VActionDataListRender();
}
function VAuctionDataListDateASC() {
    VAuctionDataList = VAuctionDataList.sort((left, right) => new Date(left.soldDate) - new Date(right.soldDate));
    VActionDataListRender();
}
function VAuctionDataListDateDESC() {
    VAuctionDataList = VAuctionDataList.sort((left, right) => new Date(right.soldDate) - new Date(left.soldDate));
    VActionDataListRender();
}
function VActionItemDataListRender() {
    $("#dynamicAuctionItemTable").empty();

    let tableStr = "";
    VAuctionItemDataList.forEach((item,index) => {
        tableStr +=
            `<tr>
                <td><img src="https://img-api.neople.co.kr/df/items/${item.itemId}" style="width: 50px; height: 50px;"></td>
                <td>${ReinforceRender(item.reinforce)}</td>
                <td>${RefineRender(item.refine)}</td>
                <td style="${ItemRarityRender(item.itemRarity)}">${item.itemName} ${UpgradeRender(item.upgrade)}</td>
                <td>${item.expireDate}</td>
                <td>${item.count}</td>
                <td>${AmountRender(item.unitPrice * item.count)}</td>
            </tr>`;
    });

    $("#dynamicAuctionItemTable").append(tableStr);
}

async function SearchAuctionSearch() {

    const response = await GetJSON("../api/AuctionSearch");
    console.log(response);

    if (response.result === false)
        return;

    if (auctionSearchChart)
        auctionSearchChart.destroy();

    auctionSearchChart = document.getElementById('auctionSearchChart');

    const dataSetList = [];
    const dataLabelList = [];

    VAuctionSearchDataList = response.data;

    const upperChartData = {};
    upperChartData.label = "검색 수";
    upperChartData.borderColor = "rgb(255,0,0)"
    upperChartData.backgroundColor = 'rgba(255,0,0,0.5)'
    upperChartData.borderWidth = 1;
    upperChartData.data = [];

    VAuctionSearchDataList.forEach((item) => {
        dataLabelList.push(item.itemName);
        upperChartData.data.push(item.searchCnt);
    });

    dataSetList.push(upperChartData);

    const data = {};
    data.datasets = dataSetList;
    data.labels = dataLabelList;

    const config = {
        type: 'bar',
        data: data,
        options: {
            type: 'myScale',
            responsive: true,
            plugins: {
                legend: {
                    position: 'top',
                },
                title: {
                    display: true,
                    text: "금일 실시간 검색 순위"
                }
            }
        },
    };

    auctionSearchChart = new Chart(auctionSearchChart, config);

    VActionSearchDataListRender();
}

function VActionSearchDataListRender() {
    $("#dynamicAuctionSearchTable").empty();

    let tableStr = "";
    VAuctionSearchDataList.forEach((item,index) => {
        tableStr +=
            `<tr>
                <td>${index + 1}</td>
                <td><img src="https://img-api.neople.co.kr/df/items/${item.itemId}" style="width: 50px; height: 50px;"></td>
                <td>${item.itemName}</td>
                <td>${item.searchCnt}</td>
            </tr>`;
    });

    $("#dynamicAuctionSearchTable").append(tableStr);
}


function VAuctionDataItemListCostASC() {
    VAuctionItemDataList = VAuctionItemDataList.sort((left, right) => left.currentPrice - right.currentPrice);
    VActionItemDataListRender();
}
function VAuctionDataItemListCostDESC() {
    VAuctionItemDataList = VAuctionItemDataList.sort((left, right) => right.currentPrice - left.currentPrice);
    VActionItemDataListRender();
}
function VAuctionDataItemListDateASC() {
    VAuctionItemDataList = VAuctionItemDataList.sort((left, right) => new Date(left.expireDate) - new Date(right.expireDate));
    VActionItemDataListRender();
}
function VAuctionDataItemListDateDESC() {
    VAuctionItemDataList = VAuctionItemDataList.sort((left, right) => new Date(right.expireDate) - new Date(left.expireDate));
    VActionItemDataListRender();
}