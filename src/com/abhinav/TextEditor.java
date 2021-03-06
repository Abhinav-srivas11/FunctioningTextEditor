package com.abhinav;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TextEditor extends JFrame
{
    final int WIDTH = 600;
    final int HEIGHT = 400;
    //these variable defined in bigger scope to allow all functions to access their values
    String foundText;
    String loadedFile = null;
    int index = 0; // this is for getting the index of matched word from list
    ArrayList<Integer> keysList = new ArrayList<>(); // this is to store all the indexes where words were matched
    ArrayList<String> keysListString = new ArrayList<>(); // this is to store all the indexes where words were matched
    //making the indexes into a list allows for easy iteration from next to previous search
    JTextArea textArea;
    Container container;
    JTextField filenameField;
    JCheckBox searchWithRegex;
    int i=0; // this variable is to control which word is being focused on based on the next or previous button

    public TextEditor()
    {
        super("Text Editor v.2.3");

        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        container = getContentPane();

        //Add a text box
        textArea = new JTextArea();
        textArea.setName("TextArea");

        //Make the current field scrollable
        JScrollPane scrollableTextArea = new JScrollPane(textArea);
        scrollableTextArea.setName("ScrollPane");
        //We indicate that the scroll will always be available
        scrollableTextArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollableTextArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        //We collect all the panels for display
        container.add(scrollableTextArea, BorderLayout.CENTER);
        container.add(GuiMenuRegion(), BorderLayout.NORTH);
        setJMenuBar(ourMenuBar());
        container.add(new JLabel(" "), BorderLayout.SOUTH);    //Just for beauty
        container.add(new JLabel("    "), BorderLayout.WEST);  //Just for beauty
        container.add(new JLabel("    "), BorderLayout.EAST);  //Just for beauty
        //Display window
        setVisible(true);
    }

    //Top area with file name field and SAVE and LOAD buttons
    private JPanel GuiMenuRegion(){
        JPanel GuiMenuRegionPanelObject = new JPanel();
        GuiMenuRegionPanelObject.setLayout(new FlowLayout(FlowLayout.CENTER));
        filenameField = new JTextField(30);
        filenameField.setName("FilenameField");

        //Defining icons for the buttons
//        ImageIcon loadIcon = new ImageIcon("").getImage().getScaledInstance(64, 64,java.awt.Image.SCALE_SMOOTH);

        //Save to file
        JButton saveButton = new JButton(new ImageIcon(((new ImageIcon(
                ".\\src\\com\\abhinav\\images\\save.png").getImage()
                .getScaledInstance(25, 25,
                        java.awt.Image.SCALE_SMOOTH))))); // this helped to scale down the image to needed size
        saveButton.setBorder(null); //this helped in removing all spaces around the logo
        saveButton.setName("SaveButton");

        //Define functioning of the Save button
        saveButton.addActionListener(actionEvent -> {
//            File file = new File(filenameField.getText());
//
//            try (FileWriter writer = new FileWriter(file)) {
//                writer.write(textArea.getText());
//            } catch (IOException e) {
//                JOptionPane.showMessageDialog(container,
//                        "ERROR!\nUnable to create file\n " + filenameField.getText());
//            }
            saveFunction();
        });

        //Upload a file with a name in the text box
        JButton loadButton = new JButton(new ImageIcon(((new ImageIcon(
                ".\\src\\com\\abhinav\\images\\upload_1.png").getImage()
                .getScaledInstance(25, 25,
                        java.awt.Image.SCALE_SMOOTH))))); // this helped to scale down the image to needed size
        loadButton.setBorder(null); //this helped in removing all spaces around the logo
        loadButton.setName("OpenButton");
        //Define the function of Load Button
        loadButton.addActionListener(actionEvent -> loadFileFunction());
        //adding more components to the buttons and file input field
        JButton searchButton = new JButton(new ImageIcon(((new ImageIcon(
                ".\\src\\com\\abhinav\\images\\search.png").getImage()
                .getScaledInstance(25, 25,
                        java.awt.Image.SCALE_SMOOTH))))); // this helped to scale down the image to needed size
        searchButton.setBorder(null); //this helped in removing all spaces around the logo
        searchButton.setName("StartSearchButton");
        searchButton.addActionListener(search -> searchAlgorithm());

        //previous search button
        JButton previousSearchButton = new JButton(new ImageIcon(((new ImageIcon(
                ".\\src\\com\\abhinav\\images\\previous.png").getImage()
                .getScaledInstance(25, 25,
                        java.awt.Image.SCALE_SMOOTH))))); // this helped to scale down the image to needed size
        previousSearchButton.setBorder(null); //this helped in removing all spaces around the logo
        previousSearchButton.setName("PreviousSearchButton");
        previousSearchButton.addActionListener(e -> back());
        //next search button
        JButton nextSearchButton = new JButton(new ImageIcon(((new ImageIcon(
                ".\\src\\com\\abhinav\\images\\next.png").getImage()
                .getScaledInstance(25, 25,
                        java.awt.Image.SCALE_SMOOTH))))); // this helped to scale down the image to needed size
        nextSearchButton.setBorder(null); //this helped in removing all spaces around the logo
        nextSearchButton.setName("NextSearchButton");
        nextSearchButton.addActionListener(e -> front());

        //Adding a regex match option to our text editor
        searchWithRegex = new JCheckBox("Use Regex");
        searchWithRegex.setName("MenuUseRegExp");


        // We assemble the panel from the name input field and 2 buttons
        //the order of adding the elements decides how they appear on GUI
        GuiMenuRegionPanelObject.add(saveButton);
        GuiMenuRegionPanelObject.add(loadButton);
        GuiMenuRegionPanelObject.add(filenameField);
        GuiMenuRegionPanelObject.add(searchButton);
        GuiMenuRegionPanelObject.add(previousSearchButton);
        GuiMenuRegionPanelObject.add(nextSearchButton);
        GuiMenuRegionPanelObject.add(searchWithRegex);

        return GuiMenuRegionPanelObject;
    }

    private void loadFileFunction() {
        try{
            //JFileChooser being used to select file from the user directory
            JFileChooser loadingFile = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            int returnValue = loadingFile.showOpenDialog(null);

            // the parent component is given as null, which means that there is no reference for the window.
            //The file chooser window will open in the middle of the user's screen

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                loadedFile = loadingFile.getSelectedFile().getAbsolutePath();
                this.setTitle(loadingFile.getSelectedFile().getName());
                textArea.setText(new String(Files.readAllBytes(Paths.get(loadingFile.getSelectedFile().getAbsolutePath()))));
            }

        } catch (IOException e) {
//                If you open - there will be a cool window with an error. But the test does not know how to use it.
//                JOptionPane.showMessageDialog(container,
//                        "ERROR!\nFile not found:\n" + filenameField.getText());
            textArea.setText(null);
        }
    }

    private void saveFunction(){
        try {
//            JFileChooser savingFile = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
//            int returnValue = savingFile.showSaveDialog(null);

//            if(returnValue == JFileChooser.APPROVE_OPTION){
//                File selectedFile = savingFile.getSelectedFile();
            File selectedFile = new File(loadedFile);
                System.out.println(selectedFile);
                FileWriter writer = new FileWriter(selectedFile);
                writer.write(textArea.getText());
                writer.close();
//            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveAsFunction(){
        try {
            JFileChooser savingFile = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            int returnValue = savingFile.showSaveDialog(null);

            if(returnValue == JFileChooser.APPROVE_OPTION){
                File selectedFile = savingFile.getSelectedFile();
            System.out.println(selectedFile);
            FileWriter writer = new FileWriter(selectedFile);
            writer.write(textArea.getText());
            writer.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private JMenuBar ourMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        //creating options inside the menubar
        JMenu fileOption = new JMenu("File");
        fileOption.setMnemonic(KeyEvent.VK_F);
        fileOption.setName("MenuFile");

        JMenu searchOption = new JMenu("Search");
        searchOption.setMnemonic(KeyEvent.VK_S);
        searchOption.setName("MenuSearch");

        menuBar.add(fileOption);
        menuBar.add(searchOption);
        //above option is what we will be visible always in the GUI window
        JMenuItem saveOption = new JMenuItem("Save");
        saveOption.setName("MenuSave");

        JMenuItem saveAsOption = new JMenuItem("Save As");
//        saveOption.setName("MenuSave");

        JMenuItem loadOption = new JMenuItem("Load");
        loadOption.setName("MenuLoad");

        JMenuItem exitOption = new JMenuItem("Exit");
        exitOption.setName("MenuExit");

        JMenuItem startSearchOption = new JMenuItem("Start Search");
        startSearchOption.setName("MenuStartSearch");

        JMenuItem nextSearchOption = new JMenuItem("Next Search");
        nextSearchOption.setName("MenuNextMatch");

        JMenuItem previousSearchOption = new JMenuItem("Previous Search");
        previousSearchOption.setName("MenuPreviousMatch");

        JMenuItem regexOption = new JMenuItem("Use Regex");
        regexOption.setName("MenuUseRegExp");

        //add all items to the menu
        fileOption.add(saveOption);
        fileOption.add(loadOption);
        fileOption.add(saveAsOption);
        searchOption.add(startSearchOption);
        searchOption.add(nextSearchOption);
        searchOption.add(previousSearchOption);
        searchOption.add(regexOption);
        fileOption.addSeparator();
        fileOption.add(exitOption);

        //adding action listeners to each menu item
        saveOption.addActionListener(saveEvent -> saveFunction());

        saveAsOption.addActionListener(saveAsEvent -> saveAsFunction());

        loadOption.addActionListener(loadEvent -> loadFileFunction());

        exitOption.addActionListener(exitEvent -> dispose());

        startSearchOption.addActionListener(startSearch -> searchAlgorithm());

        nextSearchOption.addActionListener(nextSearch -> front());

        previousSearchOption.addActionListener(previousSearch -> back());

        regexOption.addActionListener(useRegex -> searchWithRegex.setSelected(true));

        return menuBar;
    }

    private void searchAlgorithm() {

        foundText = filenameField.getText();
        System.out.println(foundText);
        Pattern pattern = Pattern.compile(foundText, Pattern.CASE_INSENSITIVE);
        Matcher match = pattern.matcher(textArea.getText());
        String regex = "[a-zA-Z]+";
        if (foundText.matches(regex)) {
            keysList.clear();
            keysListString.clear();
            while (match.find()) {
                keysList.add(match.start()); // this will the index to a list of keys
                keysListString.add(match.group());
            }
            setFocus(0);
        } else if(searchWithRegex.isSelected()){
            keysListString.clear();
            keysList.clear();
            while (match.find()) {
                keysList.add(match.start()); // this will the index to a list of keys
                keysListString.add(match.group());
            }
            setFocus(0);
        }else {
            JOptionPane.showMessageDialog(container,
                        "ERROR!\nNot a valid search string. Enter Again!\n ");
            }

    }

//    private void searching() {
//        foundText = filenameField.getText();
//        keysList.clear();
//        Scanner sc2 = null;
//        try {
//            sc2 = new Scanner(new File(loadedFile));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        while (sc2.hasNextLine()) {
//            Scanner s2 = new Scanner(sc2.nextLine());
//            while (s2.hasNext()) {
//                String s = s2.next();
//                if(s.matches(foundText)){
//                keysList.add(s2.indexOf(s));
//            }
//        }
//        }
//        setFocus(keysListString.get(0));
//        System.out.println(keysListString.get(0));
//    }

    private void front(){
        System.out.println("i value " + i);
        if(i < keysList.size()-1){
            System.out.println("sizer"+keysList.size());
        setFocus(++i);
        System.out.println(i);}
        else{
            i=0;
            setFocus(0);
        }
    }

    private void back(){
        System.out.println("i value " + i);
        if(i < 0 || i == 0){
            System.out.println("sizer"+keysList.size());
            i = keysList.size()-1;
            setFocus(i);
            System.out.println(i);}
        else {
            setFocus(--i);
        }
    }
    /* ek function that will set the variable and can be called from anywhere*/
    public void setFocus(int i){
        System.out.println(i);
        index = keysList.get(i);
        foundText = keysListString.get(i);
        textArea.setCaretPosition(index + foundText.length());
        textArea.select(index, index + foundText.length());
        textArea.grabFocus();
    }
}
