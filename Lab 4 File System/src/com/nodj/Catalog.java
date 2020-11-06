package com.nodj;

import java.util.ArrayList;

public class Catalog extends File{
    private ArrayList<File> files = new ArrayList<>();

    public Catalog(String name) {
        super(name, 1);
    }

    public boolean addFile(File f){
        for (File file:
             files) {
            if(f.getName().equals(file.getName())){
                if(f.getClass() == file.getClass()){
                    return false;
                }
            }
        }
        files.add(f);
        return true;
    }

    public boolean deleteFile(File f, FileSystem fs){
        for (File file:
                files) {
            if(f.getName().equals(file.getName())){
                if(f.getClass() == Catalog.class && file.getClass() == Catalog.class){
                    Catalog catalog = (Catalog) f;
                    for (int i = 0; i < catalog.files.size(); i++) {
                        catalog.deleteFile(catalog.files.get(i), fs);
                        i--;
                    }
                }
                fs.deleteFromDisk(f);
                files.remove(f);
                return true;
            }
        }
        return false;
    }

    public ArrayList<File> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<File> files) {
        this.files = files;
    }

    public int getFullSize() {
        int fullSize = getSize();
        for (File f: files){
            if (f.getClass() == Catalog.class){
                fullSize += ((Catalog)f).getFullSize();
            }
            else {
                fullSize += f.getSize();
            }
        }
        return fullSize;
    }
}
