package com.nodj;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.tree.DefaultMutableTreeNode;

public class App {
    public JFrame frame;
    private FileSystem fileSystem;
    private FileSystemPanel panel;
    private JTree fileManagerTree;
    private FileManager fileManager;

    /**
     * Launch the application.
     */
    App() {
        initialize();
    }

    private void initialize() {
        int width = 300;
        int height = 600;
        Disk disk = new Disk(1200, 1);
        fileSystem = new FileSystem(disk);
        fileManager = new FileManager(this, disk, fileSystem);
        panel = new FileSystemPanel(fileSystem);
        frame = new JFrame();
        frame.setName("Файловый менеджер");
        frame.setBounds(100, 100, 1000, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        panel.setBorder(new BevelBorder(BevelBorder.LOWERED,
                null, null, null, null));

        JScrollPane scrollPanel = new JScrollPane(panel);
        frame.getContentPane().add(scrollPanel);
        scrollPanel.setBounds(10, 11, width, height);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(fileSystem.getRoot(), true);
        fileManagerTree = new JTree(root);
        root.add(new DefaultMutableTreeNode(new File("", 0)));

        JScrollPane scrollTree = new JScrollPane(fileManagerTree);
        frame.getContentPane().add(scrollTree);
        scrollTree.setBounds(500, 30, 240, 300);
        fileManagerTree.addTreeSelectionListener(e -> {
            fileManager.elementChosen();
            panel.repaint();
        });

        JButton addFileButton = new JButton("Add File");
        addFileButton.addActionListener(e -> {
            fileManager.addNewFile();
            panel.repaint();
        });
        addFileButton.setBounds(width + 50, 10, 100, 50);
        frame.getContentPane().add(addFileButton);

        JButton addCatalogButton = new JButton("Add Catalog");
        addCatalogButton.addActionListener(e -> {
            fileManager.addNewCatalog();
            panel.repaint();
        });
        addCatalogButton.setBounds(width + 50, 70, 100, 50);
        frame.getContentPane().add(addCatalogButton);

        JButton deleteFileButton = new JButton("Delete");
        deleteFileButton.setBounds(width + 50, 130, 100, 50);
        frame.getContentPane().add(deleteFileButton);
        deleteFileButton.addActionListener(e -> {
            fileManager.delete();
            panel.repaint();
        });

        JButton copyFileButton = new JButton("Copy");
        copyFileButton.setBounds(width + 50, 190, 100, 50);
        frame.getContentPane().add(copyFileButton);
        copyFileButton.addActionListener(e -> fileManager.copy());

        JButton pasteFileButton = new JButton("Paste");
        pasteFileButton.setBounds(width + 50, 250, 100, 50);
        frame.getContentPane().add(pasteFileButton);
        pasteFileButton.addActionListener(e -> {
            fileManager.paste();
            panel.repaint();
        });
    }

    public JTree getFileManagerTree() {
        return fileManagerTree;
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }
}
