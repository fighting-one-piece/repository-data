/**
 * Created with IntelliJ IDEA.
 * User: AzraelX
 * Date: 13-10-22
 * Time: ����4:33
 * To change this template use File | Settings | File Templates.
 */

function loadopt(channel){
    $.ajax({
        url: "/demo/getTimeSec",
        type: "GET",
        data: ({channel:channel}),
        dataType: "json",
        error: function (xhr, ajaxOptions, thrownError){
            alert("Http status: " + xhr.status + " " + xhr.statusText + "\najaxOptions: " + ajaxOptions + "\nthrownError:"+thrownError + "\n" +xhr.responseText);
        },
        success: function(data){
//            var jsonObj=eval("("+data.result+")");
            $.each(data.result, function (i, str) {
                if(document.getElementById("selectedTime").innerHTML.split(' ')[1] == str.split("--")[1]){
//                if(document.getElementsByClassName("time-info").item(0).innerText.split(' ')[1] == str.split("--")[1]){
                    $("#tsec").append("<option selected=\"selected\" value='"+str+"'>"+str.split('--')[1]+"</option>");
                } else {
                    $("#tsec").append("<option value='"+str+"'>"+str.split('--')[1]+"</option>");
                }
//                $("#tsec").append("<option value='"+str+"'>"+str.split('--')[1]+"</option>");
            });
//            for(str in data.result){
//                $("#tsec").append("<option value='"+str+"'>"+str+"</option>");
//            }
        }
    });
}

function visit(channel){
    var tsec = $("#tsec").val();
    var today = new Date();
    var tim =tsec.split('--');
//    var year = today.getFullYear().toString();
//    var mon = (today.getMonth()+1).toString();
//    if(mon.length<2){
//        mon = '0' + mon;
//    }
//    var day =today.getDate().toString();
//    var tdstr = $.formatDate(today,'yyyyMMdd');
//    var tdstr = year+mon+day;
    var tdstr = today.Format("yyyyMMdd");
//    var filename = tdstr+"/"+tdstr+tim[0].replace(/:/g,"")+"-"+tdstr+tim[1].replace(/:/g,"")+".html";
    var filename = "/"+channel+"/"+tdstr+"/"+tdstr+tim[0].replace(/:/g,"")+"-"+tdstr+tim[1].replace(/:/g,"")+".html";
    window.open(filename,"_self");
}

function jpage(){
    var tsec = $("#tsec").val();
    var today = new Date();
    var tim =tsec.split('--');
//    var year = today.getFullYear().toString();
//    var mon = (today.getMonth()+1).toString();
//    if(mon.length<2){
//        mon = '0' + mon;
//    }
//    var day =today.getDate().toString();
//    var tdstr = $.formatDate(today,'yyyyMMdd');
//    var tdstr = year+mon+day;
    var tdstr = today.Format("yyyyMMdd");
//    var filename = tdstr+"/"+tdstr+tim[0].replace(/:/g,"")+"-"+tdstr+tim[1].replace(/:/g,"")+".html";
    var filename = tdstr+tim[0].replace(/:/g,"")+"-"+tdstr+tim[1].replace(/:/g,"")+".html";
    window.open(filename,"_self");
}

// ��Date����չ���� Date ת��Ϊָ����ʽ��String
// ��(M)����(d)��Сʱ(h)����(m)����(s)������(q) ������ 1-2 ��ռλ����
// ��(y)������ 1-4 ��ռλ��������(S)ֻ���� 1 ��ռλ��(�� 1-3 λ������)
// ���ӣ�
// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
// (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18
Date.prototype.Format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1, //�·�
        "d+": this.getDate(), //��
        "h+": this.getHours(), //Сʱ
        "m+": this.getMinutes(), //��
        "s+": this.getSeconds(), //��
        "q+": Math.floor((this.getMonth() + 3) / 3), //����
        "S": this.getMilliseconds() //����
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}