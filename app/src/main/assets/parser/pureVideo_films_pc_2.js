javascript:window.customScript.log('==========start===========');
var child = document.children;
var arr = [];
function fn(obj){
    for(var i=0;i<obj.length;i++){
        if(obj[i].children){
            fn(obj[i].children);
        }
        arr.push(obj[i]);
    }
}
fn(child);
var head = document.getElementsByTagName('head')[0];
var tb_player = document.getElementsByClassName('tb player')[0];
var player_box = document.getElementsByClassName('player-box')[0];
var player_ff = document.getElementsByClassName('player-ff')[0];
for(var i=0;i<arr.length;i++){
    var tagName = arr[i].tagName;
    var id = arr[i].id;
    var className = arr[i].className;
    if(tagName.indexOf('HTML') != -1 || tagName.indexOf('BODY') != -1
    || (tagName.indexOf('HEAD') != -1 && tagName.indexOf("HEADER") == -1)
    || className.indexOf('player-box') != -1
    || className.indexOf('player-ff') != -1
    || className.indexOf('tb player') != -1
    || arr[i].parentNode == head
    || arr[i].parentNode == tb_player
    || arr[i].parentNode == player_box
    || arr[i].parentNode == player_ff
    ){
        window.customScript.log(' tagName = '+tagName+" ,id = "+id);
        continue;
    }
    arr[i].parentNode.removeChild(arr[i]);
}
var html = document.getElementsByTagName('html')[0];
window.customScript.log('html : '+html.innerHTML);
window.customScript.onJSLoadComplete();