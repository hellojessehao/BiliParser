javascript:window.customScript.log('==========start===========');
var header = document.getElementsByClassName('header')[0];
if(header){
    header.parentNode.removeChild(header);
}
var mask = document.getElementsByClassName('mask')[0];
if(mask){
    mask.parentNode.removeChild(mask);
}
var list_nav = document.getElementsByClassName('list_nav')[0];
if(list_nav){
    list_nav.parentNode.removeChild(list_nav);
}
var tbmov_notice = document.getElementsByClassName('tbmov-notice')[0];
if(tbmov_notice){
    tbmov_notice.parentNode.removeChild(tbmov_notice);
}
var box = document.getElementById('box');
if(box){
    box.parentNode.removeChild(box);
}
var vod_play_tab = document.getElementsByClassName('vod-play-tab')[0];
if(vod_play_tab){
    vod_play_tab.parentNode.removeChild(vod_play_tab);
}
var vod_play_info = document.getElementsByClassName('vod-play-info')[0];
if(vod_play_info){
    vod_play_info.parentNode.removeChild(vod_play_info);
}
window.customScript.onJSLoadComplete();
//var videoParent = document.getElementById('mobile-index');
//videoParent.setAttribute('style','position:fixed;align-items: \"center\";');