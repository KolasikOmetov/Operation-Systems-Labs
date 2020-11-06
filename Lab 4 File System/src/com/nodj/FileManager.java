package com.nodj;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;

public class FileManager {
    private final App parent;
    private DefaultMutableTreeNode buffer;
    private final Disk disk;
    private final FileSystem fileSystem;

    public FileManager(App parent, Disk disk, FileSystem fileSystem) {
        this.parent = parent;
        this.disk = disk;
        this.fileSystem = fileSystem;
    }

    public void elementChosen() {
        for (Sector sector : disk.getSectorsArray()) {
            if (sector.getSectorState() == SectorStatus.SELECTED) {
                sector.setSectorStatus(SectorStatus.FILLED);
            }
        }

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getFileManagerTree().getLastSelectedPathComponent();

        if (node == null)
            return;

        File chosenFile = (File) node.getUserObject();
        int curINode = chosenFile.getINode();
        while (curINode != -1) {
            disk.getSectorsArray()[curINode].setSectorStatus(SectorStatus.SELECTED);
            curINode = parent.getFileSystem().getClustersArray()[curINode];
        }
    }

    public void addNewFile() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getFileManagerTree().getLastSelectedPathComponent();
        if (node == null || !node.getAllowsChildren() || node.isLeaf()) {
            JOptionPane.showConfirmDialog(parent.frame, "Нужно выбрать каталог, где будет новый файл",
                    "Ошибка", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nameFile;
        while (true) {
            nameFile = JOptionPane.showInputDialog(parent.frame, "Введите название файла");
            if (nameFile == null) {
                return;
            } else if (nameFile.equals("")) {
                JOptionPane.showMessageDialog(parent.frame, "Пустое название недопустимо", "Добавление файла", JOptionPane.INFORMATION_MESSAGE);
            } else {
                break;
            }
        }
        int sizeFile;
        while (true) {
            try {
                sizeFile = Integer.parseInt(JOptionPane.showInputDialog(parent.frame, "Введите размер файла"));
                if (sizeFile < disk.getSectorSize()) {
                    throw new Exception();
                } else {
                    break;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent.frame, "Были введены некорректные данные", "Добавление файла", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        File newFile = new File(nameFile, sizeFile);
        JOptionPane.showMessageDialog(parent.frame, fileSystem.addFile((Catalog) node.getUserObject(), newFile), "Добавление файла", JOptionPane.INFORMATION_MESSAGE);

        if (!fileSystem.isFailed()) {
            fileSystem.setFailed(true);
            DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(newFile, false);
            if (((File) (((DefaultMutableTreeNode) node.getChildAt(0)).getUserObject())).getName().equals("")) {
                node.remove(0);
            }
            node.add(fileNode);
            parent.getFileManagerTree().updateUI();
            elementChosen();
            parent.getFileManagerTree().expandPath(new TreePath(node.getPath()));
        }
    }

    public void addNewCatalog() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getFileManagerTree().getLastSelectedPathComponent();
        if (node == null || !node.getAllowsChildren()) {
            JOptionPane.showConfirmDialog(parent.frame, "Нужно выбрать каталог, где будет новый каталог",
                    "Ошибка", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nameCatalog;
        while (true) {
            nameCatalog = JOptionPane.showInputDialog(parent.frame, "Введите название каталога");
            if (nameCatalog == null) {
                return;
            } else if (nameCatalog.equals("")) {
                JOptionPane.showMessageDialog(parent.frame, "Пустое название недопустимо", "Добавление файла", JOptionPane.INFORMATION_MESSAGE);
            } else {
                break;
            }
        }

        Catalog newCatalog = new Catalog(nameCatalog);
        JOptionPane.showMessageDialog(parent.frame, fileSystem.addFile((Catalog) node.getUserObject(), newCatalog), "Добавление файла", JOptionPane.INFORMATION_MESSAGE);

        if (!fileSystem.isFailed()) {
            fileSystem.setFailed(true);
            if (((File) (((DefaultMutableTreeNode) node.getChildAt(0)).getUserObject())).getName().equals("")) {
                node.remove(0);
            }
            DefaultMutableTreeNode catalogNode = new DefaultMutableTreeNode(newCatalog, true);
            catalogNode.add(new DefaultMutableTreeNode(new File("", 0)));
            node.add(catalogNode);
            parent.getFileManagerTree().updateUI();
            elementChosen();
            parent.getFileManagerTree().expandPath(new TreePath(node.getPath()));
        }
    }

    public void delete() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getFileManagerTree().getLastSelectedPathComponent();
        if (node == null || node.getParent() == null) {
            JOptionPane.showConfirmDialog(parent.frame, "Выберите файл, который хотите удалить",
                    "Ошибка", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(parent.frame, "Вы действительно хотите удалить файл " + node.getUserObject() + "?",
                "Удаление файла", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
            JOptionPane.showMessageDialog(parent.frame, fileSystem.deleteFile((Catalog) parentNode.getUserObject(), (File) node.getUserObject()), "Удаление файла", JOptionPane.INFORMATION_MESSAGE);
            if (!fileSystem.isFailed()) {
                fileSystem.setFailed(true);
                parentNode.remove(node);
                if (parentNode.getChildCount() == 0) {
                    parentNode.add(new DefaultMutableTreeNode(new File("", 0)));
                }
                parent.getFileManagerTree().updateUI();
                parent.getFileManagerTree().expandPath(new TreePath(node.getPath()));
            }
        }
    }

    public void copy() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getFileManagerTree().getLastSelectedPathComponent();

        if (node == null || node.getParent() == null) {
            JOptionPane.showConfirmDialog(parent.frame, "Выберите файл, который хотите скопировать",
                    "Ошибка", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
            return;
        }

        buffer = node;
        JOptionPane.showMessageDialog(parent.frame, "Файл " + node.getUserObject() + " скопирован!",
                "Копирование", JOptionPane.INFORMATION_MESSAGE);
    }

    public void paste() {
        if (buffer != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getFileManagerTree().getLastSelectedPathComponent();

            if (node == null || !node.getAllowsChildren() || node.isLeaf()) {
                JOptionPane.showConfirmDialog(parent.frame, "Выберите каталог, куда желаете вставить файл",
                        "Ошибка", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
                return;
            }

            DefaultMutableTreeNode newNode = cloneNode(buffer);

            File newFile = (File) newNode.getUserObject();

            JOptionPane.showMessageDialog(parent.frame, fileSystem.addFile((Catalog) node.getUserObject(), newFile), "Вставка файла", JOptionPane.INFORMATION_MESSAGE);

            if (!fileSystem.isFailed()) {
                if (((File) (((DefaultMutableTreeNode) node.getChildAt(0)).getUserObject())).getName().equals("")) {
                    node.remove(0);
                }
                fileSystem.setFailed(true);
                if(newFile.getClass() == Catalog.class){
                    node.add(makeTree(new DefaultMutableTreeNode(newFile, true)));
                }
                else {
                    node.add(new DefaultMutableTreeNode(newFile, false));
                }

                parent.getFileManagerTree().updateUI();
                elementChosen();
                parent.getFileManagerTree().expandPath(new TreePath(node.getPath()));
                JOptionPane.showMessageDialog(parent.frame, "Вставка прошла успешно", "Вставка файла", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showConfirmDialog(parent.frame, "Вы не скопировали файл",
                    "Ошибка", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
        }

    }

    private DefaultMutableTreeNode makeTree(DefaultMutableTreeNode node) {
        Catalog c = (Catalog) node.getUserObject();
        if (c.getFiles().size() == 0){
            node.add(new DefaultMutableTreeNode(new File("", 0), false));
            return node;
        }
        for (File f: c.getFiles()){
            if (f.getClass() == Catalog.class){
                node.add(makeTree(new DefaultMutableTreeNode(f, true)));
            }
            else {
                node.add(new DefaultMutableTreeNode(f, false));
            }
        }
        return node;
    }

    private DefaultMutableTreeNode cloneNode(DefaultMutableTreeNode node) {
        File nodeFile = (File) node.getUserObject();
        DefaultMutableTreeNode newNode;
        if (nodeFile.getClass() != Catalog.class) {
            newNode = new DefaultMutableTreeNode(new File(node.toString(), nodeFile.getSize()));
        } else {
            Catalog catalog = new Catalog(node.toString());
            catalog.setFiles(new ArrayList<>(((Catalog)nodeFile).getFiles()));
            newNode = new DefaultMutableTreeNode(catalog);
            for (int i = 0; i < node.getChildCount(); i++) {
                if (node.getChildAt(i).isLeaf()) {
                    DefaultMutableTreeNode nodeChild = (DefaultMutableTreeNode) node.getChildAt(i);
                    File file = catalog.getFiles().get(i);
                    newNode.add(new DefaultMutableTreeNode(new File(nodeChild.toString(), file.getSize())));
                } else {
                    newNode.add(cloneNode((DefaultMutableTreeNode) node.getChildAt(i)));
                }
            }
        }
        return newNode;
    }

    private void allocateCatalog(DefaultMutableTreeNode node, DefaultMutableTreeNode parentNode) {
        Catalog parentCatalog = (Catalog) parentNode.getUserObject();
        if (node.getUserObject().getClass() != Catalog.class) {
            return;
        }
        Catalog catalog = (Catalog) node.getUserObject();

        String message = fileSystem.addFile(parentCatalog, catalog);
        if (fileSystem.isFailed()) {
            JOptionPane.showMessageDialog(parent.frame, message, "Вставка файла", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
//        for (int i = 0; i < node.getChildCount(); i++) {
//            DefaultMutableTreeNode nodeChild = (DefaultMutableTreeNode) node.getChildAt(i);
//            if (node.getChildAt(i).isLeaf()) {
//                File file = (File) nodeChild.getUserObject();
//                String messageErr = fileSystem.addFile(catalog, file);
//                if (fileSystem.isFailed()) {
//                    JOptionPane.showMessageDialog(parent.frame, messageErr, "Вставка файла", JOptionPane.INFORMATION_MESSAGE);
//                    return;
//                }
//            } else {
//                allocateCatalog(nodeChild, node);
//            }
//        }
    }
}
