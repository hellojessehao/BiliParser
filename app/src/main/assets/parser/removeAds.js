javascript:window.customScript.getHtml('==========script test start===========');
var divList = document.getElementsByTagName('div');
window.customScript.getHtml('==========div size : ==========='+divList.length);
for(var i=0;i<divList.length;i++){
    var className = divList[i].className;
    window.customScript.getHtml('==========className : ==========='+className);
    if(className == '' || (className.indexOf('player') == -1 && className.indexOf('playbox') == -1)
    || className.indexOf('-') != -1){
        window.customScript.getHtml('==========remove div i : ==========='+i);
        divList[i].parentNode.removeChild(divList[i]);
    }
}
var headerList = document.getElementsByTagName('header');
window.customScript.getHtml('==========headerList size : ==========='+headerList.length);
for(var i=0;i<headerList.length;i++){
    headerList[i].parentNode.removeChild(headerList[i]);
    window.customScript.getHtml('==========remove header i : ==========='+i);
}
var bottomList = document.getElementsByClassName('bottom');
window.customScript.getHtml('==========bottomList size : ==========='+bottomList.length);
for(var i=0;i<bottomList.length;i++){
    bottomList[i].parentNode.removeChild(bottomList[i]);
    window.customScript.getHtml('==========remove bottom i : ==========='+i);
}
var h2List = document.getElementsByTagName('h2');
window.customScript.getHtml('==========h2List size : ==========='+h2List.length);
for(var i=0;i<h2List.length;i++){
    h2List[i].parentNode.removeChild(h2List[i]);
    window.customScript.getHtml('==========remove h2 i : ==========='+i);
}
var ulList = document.getElementsByTagName('ul');
window.customScript.getHtml('==========ulList size : ==========='+ulList.length);
for(var i=0;i<ulList.length;i++){
    ulList[i].parentNode.removeChild(ulList[i]);
    window.customScript.getHtml('==========remove ul i : ==========='+i);
}
window.customScript.getHtml('==========script test end===========');