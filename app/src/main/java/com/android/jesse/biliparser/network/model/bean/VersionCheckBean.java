package com.android.jesse.biliparser.network.model.bean;

/**
 * @Description: fir.im版本查询接口返回参数
 * @author: zhangshihao
 * @date: 2020/4/24
 */
public class VersionCheckBean {

    /**
     * name : fir.im
     * version : 1.0
     * changelog : 更新日志
     * versionShort : 1.0.5
     * build : 6
     * installUrl : http://download.bq04.com/v2/app/install/xxxxxxxxxxxxxxxxxxxx?download_token=xxxxxxxxxxxxxxxxxxxxxxxxxxxx
     * install_url : http://download.bq04.com/v2/app/install/xxxxxxxxxxxxxxxx?download_token=xxxxxxxxxxxxxxxxxxxxxxxxxxxx
     * update_url : http://fir.im/fir
     * binary : {"fsize":6446245}
     */

    private String name;//应用名称
    private String version;//版本
    private String changelog;//更新日志
    private String versionShort;//版本编号(兼容旧版字段)
    private String build;//编译号
    private String installUrl;//安装地址（兼容旧版字段）
    private String install_url;//安装地址(新增字段)
    private String update_url;//更新地址(新增字段)
    private BinaryBean binary;//更新文件的对象，仅有大小字段fsize

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getChangelog() {
        return changelog;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }

    public String getVersionShort() {
        return versionShort;
    }

    public void setVersionShort(String versionShort) {
        this.versionShort = versionShort;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String getInstallUrl() {
        return installUrl;
    }

    public void setInstallUrl(String installUrl) {
        this.installUrl = installUrl;
    }

    public String getInstall_url() {
        return install_url;
    }

    public void setInstall_url(String install_url) {
        this.install_url = install_url;
    }

    public String getUpdate_url() {
        return update_url;
    }

    public void setUpdate_url(String update_url) {
        this.update_url = update_url;
    }

    public BinaryBean getBinary() {
        return binary;
    }

    public void setBinary(BinaryBean binary) {
        this.binary = binary;
    }

    public static class BinaryBean {
        /**
         * fsize : 6446245
         */

        private int fsize;

        public int getFsize() {
            return fsize;
        }

        public void setFsize(int fsize) {
            this.fsize = fsize;
        }
    }


}
