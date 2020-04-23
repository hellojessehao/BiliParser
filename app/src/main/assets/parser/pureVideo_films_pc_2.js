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
var macPlayer = document.getElementsByClassName('MacPlayer')[0];
for(var i=0;i<arr.length;i++){
    var tagName = arr[i].tagName;
    var id = arr[i].id;
    var className = arr[i].className;
    if(tagName.indexOf('HTML') != -1 || tagName.indexOf('BODY') != -1
    || (tagName.indexOf('HEAD') != -1 && tagName.indexOf("HEADER") == -1)
    || tagName.indexOf('TBODY') != -1 || tagName.indexOf('TR') != -1
    || id.indexOf('playleft') != -1 || tagName.indexOf('IFRAME') != -1
    || className.indexOf('player-box') != -1
    || className.indexOf('player-ff') != -1
    || className.indexOf('tb player') != -1
    || arr[i].parentNode == head
    || arr[i].parentNode == tb_player
    || arr[i].parentNode == player_box
    || arr[i].parentNode == player_ff
    || arr[i].parentNode == macPlayer
    ){
        continue;
    }
    arr[i].parentNode.removeChild(arr[i]);
}
var tab_r = document.getElementsByClassName('tab-r')[0];
if(tab_r){
    tab_r.parentNode.removeChild(tab_r);
}
var player_info = document.getElementsByClassName('player-info')[0];
if(player_info){
    player_info.parentNode.removeChild(player_info);
}
var els_sharebox = document.getElementsByClassName('els-sharebox')[0];
if(els_sharebox){
    els_sharebox.parentNode.removeChild(els_sharebox);
}
var player_vinfo = document.getElementsByClassName('player-vinfo')[0];
if(player_vinfo){
    player_vinfo.parentNode.removeChild(player_vinfo);
}
var playopen = document.getElementsByClassName('playopen')[0];
if(playopen){
    playopen.parentNode.removeChild(playopen);
}

var body = document.getElementsByTagName('body')[0];
if(tb_player){
    tb_player.className = '';
    tb_player.id = '';
}
if(player_box){
    player_box.className = '';
    player_box.id = '';
}
if(player_ff){
    player_ff.className = '';
    player_ff.id = '';
}
if(macPlayer){
    macPlayer.style.height = '1000px';
}
var iframeArr = document.getElementsByTagName('iframe');
iframeArr[0].parentNode.removeChild(iframeArr[0]);
iframeArr[0].style.height = '1000px';

var html = document.getElementsByTagName('html')[0];
window.customScript.log('html : '+html.innerHTML);
window.customScript.onJSLoadComplete();