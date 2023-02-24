package github.zimoyin.mtool.uilt.message;

import lombok.*;
import net.mamoe.mirai.contact.FileSupported;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.file.AbsoluteFile;
import net.mamoe.mirai.contact.file.AbsoluteFolder;
import net.mamoe.mirai.contact.file.RemoteFiles;
import net.mamoe.mirai.utils.ExternalResource;
import net.mamoe.mirai.utils.ProgressionCallback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@ToString
@Getter
@Builder
public class GroupFile {
    /**
     * 文件在服务器的路径
     */
    private String path;
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 是否是文件夹
     */
    private boolean isDirectory;
    /**
     * 是否是文件
     */
    private boolean isFile;
    /**
     * 父文件夹
     */
    private GroupFile parentFile;
    /**
     * 文件过期时间
     */
    private long expiryTime;
    /**
     * 文件创建时间
     */
    private long createTime;
    /**
     * 最后一次修改时间
     */
    private long lastModifiedTime;

    /**
     * 上传文件的ID
     */
    private String fileID;
    /**
     * 上传文件人的昵称
     */
    private String fileAuthor;
    /**
     * 上传文件人的QQ
     */
    private long fileAuthorID;
    //    private String fileURL;
    /**
     * 当前文件的大小
     * 或者当前文件夹的文件个数
     */
    private long size;
    private byte[] MD5 = new byte[0];
    private byte[] sha1 = new byte[0];

    /**
     * 是否是根目录
     */
    private boolean root = false;

    /**
     * 表示一个远程文件或目录.
     */
    private RemoteFiles thisRemoteFiles;
    /**
     * 表示远程文件
     */
    private AbsoluteFile thisFile;
    /**
     * 表示远程文件列表 (管理器).
     */
    private AbsoluteFolder thisFolder;
    /**
     * 绝对文件或目录标识. 精确表示一个远程文件. 不会受同名文件或目录的影响.
     */
    private FileSupported thisFileSupported;
    /**
     * 文件所在的群
     */
    private Group group;
    /**
     * 文件所在的文件系统
     */
    private GroupFileSystem system;

    private GroupFile(String path, String fileName, boolean isDirectory, boolean isFile, GroupFile parentFile, long expiryTime, long createTime, long lastModifiedTime, String fileID, String fileAuthor, long fileAuthorID, long size, byte[] MD5, byte[] sha1, boolean root, RemoteFiles thisRemoteFiles, AbsoluteFile thisFile, AbsoluteFolder thisFolder, FileSupported thisFileSupported, Group group, GroupFileSystem system) {
        this.path = path;
        this.fileName = fileName;
        this.isDirectory = isDirectory;
        this.isFile = isFile;
        this.parentFile = parentFile;
        this.expiryTime = expiryTime;
        this.createTime = createTime;
        this.lastModifiedTime = lastModifiedTime;
        this.fileID = fileID;
        this.fileAuthor = fileAuthor;
        this.fileAuthorID = fileAuthorID;
        this.size = size;
        this.MD5 = MD5;
        this.sha1 = sha1;
        this.root = root;
        this.thisRemoteFiles = thisRemoteFiles;
        this.thisFile = thisFile;
        this.thisFolder = thisFolder;
        this.thisFileSupported = thisFileSupported;
        this.group = group;
        this.system = system;
    }

    public GroupFile(String path, Group group) {
        GroupFile file = GroupFileSystem.createGroupFile(path, group);
        if (file == null) throw new NullPointerException("在该群" + group.getId() + "下没有找到所需的文件 " + path);
        buildThisGroupFile(file);
        system.getIndexMap().put(path, this);
    }


    public GroupFile(RemoteFiles files, Group group, GroupFileSystem groupFileSystem) {
        this.path = "/";
        root = true;
        isDirectory = true;
        //表示一个远程文件或目录.
        thisRemoteFiles = files;
        //表示远程文件列表 (管理器).
        thisFolder = files.getRoot();
        //绝对文件或目录标识. 精确表示一个远程文件. 不会受同名文件或目录的影响.
        thisFileSupported = files.getContact();
        this.group = group;
        this.system = groupFileSystem;
    }

    /**
     * 远程文件的文件目录
     */
    @Deprecated
    public AbsoluteFolder getRootRemoteFile() {
        return thisRemoteFiles.getRoot();
    }

    /**
     * 文件的URL
     */
    public String getURL() {
        return thisFile.getUrl();
    }

    /**
     * 获取文件作者的名称
     */
    public String getFileAuthor() {
        if (fileAuthor == null) this.fileAuthor = group.getOrFail(fileAuthorID).getNick();
        return fileAuthor;
    }

    private static GroupFile.GroupFileBuilder builder() {
        return new GroupFile.GroupFileBuilder();
    }

    /**
     * 构建当前的文件对象对其字段进行赋值。
     */
    private void buildThisGroupFile(GroupFile file) {
        this.thisFolder = file.getThisFolder();
        this.thisRemoteFiles = file.getThisRemoteFiles();
        this.thisFile = file.getThisFile();
        this.group = file.getGroup();
        this.system = file.getSystem();
        this.path = file.getPath();
        this.fileName = file.getFileName();
        this.fileID = file.getFileID();
        this.MD5 = file.getMD5();
        this.sha1 = file.getSha1();
        this.size = file.getSize();
        this.expiryTime = file.getExpiryTime();
        this.parentFile = file.getParentFile();
        this.isFile = file.isFile();
        this.isDirectory = file.isDirectory();
        this.lastModifiedTime = file.getLastModifiedTime();
        this.createTime = file.getCreateTime();
        this.fileAuthorID = file.getFileAuthorID();
    }

    public List<GroupFile> list() {
        if (isFile) throw new IllegalArgumentException("无法对一个文件进行文件夹遍历");
        List<AbsoluteFolder> collect = thisFolder.foldersStream().collect(Collectors.toList());
        List<AbsoluteFile> collect1 = thisFolder.filesStream().collect(Collectors.toList());
        List<GroupFile> list = new ArrayList<GroupFile>();
        HashMap<String, GroupFile> indexMap = this.system.getIndexMap();
        for (AbsoluteFolder file : collect) {
            GroupFile build = GroupFile.builder()
                    .thisRemoteFiles(file.getContact().getFiles())
                    .thisFolder(file)
                    .group(group)
                    .system(getSystem())
                    .path(file.getName())
                    .fileName(new File(file.getName()).getName())
                    .fileID(file.getId())
                    .size(file.getContentsCount())
                    .parentFile(this)
                    .isFile(false)
                    .isDirectory(true)
                    .lastModifiedTime(file.getLastModifiedTime())
                    .createTime(file.getUploadTime())
                    .fileAuthorID(file.getUploaderId())
                    .build();
            indexMap.put(build.getPath(), build);
            list.add(build);
        }

        for (AbsoluteFile file : collect1) {
            GroupFile build = GroupFile.builder()
                    .thisRemoteFiles(file.getContact().getFiles())
                    .thisFolder(this.thisFolder)
                    .thisFile(file)
                    .group(group)
                    .system(getSystem())
                    .path(file.getName())
                    .fileName(new File(file.getName()).getName())
                    .fileID(file.getId())
                    .MD5(file.getMd5())
                    .sha1(file.getSha1())
                    .size(file.getSize())
                    .expiryTime(file.getExpiryTime())
                    .parentFile(this)
                    .isFile(true)
                    .isDirectory(false)
                    .lastModifiedTime(file.getLastModifiedTime())
                    .createTime(file.getUploadTime())
                    .fileAuthorID(file.getUploaderId())
                    .build();
            indexMap.put(build.getPath(), build);
            list.add(build);
        }
        return list;
    }

    /**
     * 重命名
     */
    public boolean renameTo(String name) {
        //更新路径
        if (getParentFile() != null) {
            HashMap<String, GroupFile> indexMap = this.system.getIndexMap();
            indexMap.remove(this.path);
            this.path = getParentFile().getPath() + "/" + name;
            indexMap.put(this.path, this);
        }
        //更新名称
        if (this.isFile) return thisFile.renameTo(name);
        else return thisFolder.renameTo(name);
    }

    /**
     * 删除文件
     */
    public boolean delete() {
        if (this.isFile) return thisFile.delete();
        else return thisFolder.delete();
    }

    /**
     * 该方法使用效果与 linux 中的 move指令一致，请仔细判断后使用
     */
    public boolean moveTo(String path) {
        boolean move = false;
        path = path.trim();
        AbsoluteFolder folder = getRootRemoteFile().resolveFolder(path);
        if (path.indexOf("/") != 0 && path.length() <= 1) throw new IllegalArgumentException("Cannot move to " + path);
        if (folder == null) throw new IllegalArgumentException("Cannot move to " + path);
        if (this.isFile) {
            HashMap<String, GroupFile> indexMap = this.system.getIndexMap();
            indexMap.remove(this.path);
            move = thisFile.moveTo(folder);
            if (move) this.path = path;
            indexMap.put(this.path, this);
        }
        return false;
    }

    /**
     * 创建文件夹
     */
    public AbsoluteFolder mkdir(String folder) {
        if (isDirectory) {
            return thisFolder.createFolder(folder);
        }
        return null;
    }


    /**
     * 构建索引
     */
    public void buildIndex() {
        system.buildIndex();
    }

    /**
     * 上传文件，如果当前对象是文件则删除文件后再上传。如果当然对象是文件夹则直接上传
     * 注意：这不会去维护现有的索引
     *
     * @param name     文件的名称
     * @param resource 文件实例 ExternalResource.create(...) 获取，但是请注意关闭他
     * @param call     上传回调
     * @return 上传的文件
     */
    public AbsoluteFile uploadFile(String name, ExternalResource resource, ProgressionCallback<AbsoluteFile, Long> call) {
        if (name == null || resource == null) throw new NullPointerException("name and resource  must not be null");
        if (this.isFile) this.delete();
        return getThisFolder().uploadNewFile(name, resource, call);
    }

    /**
     * 上传文件，如果当前对象是文件则抛出异常。如果当然对象是文件夹则直接上传
     * 注意：这不会去维护现有的索引
     *
     * @param name     文件的名称
     * @param resource 文件实例 ExternalResource.create(...) 获取，但是请注意关闭他
     * @param call     上传回调
     * @return 上传的文件
     */
    public AbsoluteFile uploadNewFile(String name, ExternalResource resource, ProgressionCallback<AbsoluteFile, Long> call) {
        if (name == null || resource == null) throw new NullPointerException("name and resource  must not be null");
        if (this.isDirectory) return getThisFolder().uploadNewFile(name, resource, call);
        else throw new IllegalArgumentException("这个GroupFile 是一个文件对象，无法对文件对象进行上传文件操作");
    }

    /**
     * 上传文件，如果当前对象是文件则抛出异常。如果当然对象是文件夹则直接上传
     * 注意：这不会去维护现有的索引
     *
     * @param file 文件
     * @param call 上传回调
     * @return 上传的文件
     */
    public AbsoluteFile uploadNewFile(File file, ProgressionCallback<AbsoluteFile, Long> call) throws IOException {
        if (file == null) throw new NullPointerException("file  must not be null");
        try (ExternalResource resource = ExternalResource.create(file)) {
            if (this.isDirectory) return getThisFolder().uploadNewFile(file.getName(), resource, call);
            else throw new IllegalArgumentException("这个GroupFile 是一个文件对象，无法对文件对象进行上传文件操作");
        }
    }

    /**
     * 上传文件，如果当前对象是文件则抛出异常。如果当然对象是文件夹则直接上传
     * 注意：这不会去维护现有的索引
     *
     * @param path 文件路径
     * @param call 上传回调
     * @return 上传的文件
     */
    public AbsoluteFile uploadNewFile(String path, ProgressionCallback<AbsoluteFile, Long> call) throws IOException {
        if (path == null) throw new NullPointerException("path  must not be null");
        File file = new File(path);
        return uploadNewFile(file, call);
    }
}