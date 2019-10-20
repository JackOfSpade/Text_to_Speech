import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.model.OutputFormat;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.*;

class CustomDialog extends JDialog
        implements ActionListener,
        PropertyChangeListener
{

    private JTextArea textArea;
    private JOptionPane optionPane;
    private String enterString = "Enter";
    private String removeLineBreaksString = "Remove Line Breaks";
    String[] voices = {"Matthew", "Ivy", "Joanna", "Kendra", "Kimberly", "Salli", "Joey", "Justin", "Zhiyu"};
    JComboBox voiceList = new JComboBox(voices);
    Scrollbar speedBar = new Scrollbar(Scrollbar.HORIZONTAL, 0, 10, 0, 200);

    /**
     * Creates the reusable dialog.
     */
    public CustomDialog(Frame aFrame, String aWord)
    {
        super(aFrame, true);
        setTitle("Text-to-Speech");

        textArea = new JTextArea(10, 30);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(textArea.getFont().deriveFont(16f));

        JScrollPane scrollArea = new JScrollPane (textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        //Create an array of the text and components to be displayed.
        String msgString1 = "Enter Text:";
        String msgString2 = "Speech Speed (0% - 200%):";
        speedBar.setBackground(Color.black);
        speedBar.setValue(100);
        String msgString3 = "Voice:";
        Object[] array = {msgString1, scrollArea, msgString2, speedBar, msgString3, voiceList};

        //Create an array specifying the number of dialog buttons
        //and their text.
        Object[] options = {enterString, removeLineBreaksString};

        //Create the JOptionPane.
        optionPane = new JOptionPane(array,JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION,null, options, options[0]);

        //Make this dialog display it.
        this.setContentPane(optionPane);

        //Handle window closing correctly.
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        //Ensure the text field always gets the first focus.
        addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentShown(ComponentEvent ce)
            {
                textArea.requestFocusInWindow();
            }
        });

        //Register an event handler that puts the text into the option pane.
       // textField.addActionListener(this);

        //Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);
        pack();
    }

    /**
     * This method handles events for the text field.
     */
    public void actionPerformed(ActionEvent e)
    {
        optionPane.setValue(enterString);

    }

    /**
     * This method reacts to state changes in the option pane.
     */
    public void propertyChange(PropertyChangeEvent e)
    {
        String prop = e.getPropertyName();

        if (isVisible()
                && (e.getSource() == optionPane)
                && (JOptionPane.VALUE_PROPERTY.equals(prop)
                || JOptionPane.INPUT_VALUE_PROPERTY.equals(prop)))
        {
            Object value = optionPane.getValue();

            if (value == JOptionPane.UNINITIALIZED_VALUE)
            {
                //ignore reset
                return;
            }

            //Reset the JOptionPane's value.
            //If you don't do this, then if the user
            //presses the same button next time, no
            //property change event will be fired.
            optionPane.setValue(
                    JOptionPane.UNINITIALIZED_VALUE);

            //Enter button selected
            if (value.equals(enterString))
            {
                //create AmazonPolly Class
                AmazonPolly amazonPolly = new AmazonPolly(Region.getRegion(Regions.DEFAULT_REGION), voiceList.getSelectedItem().toString());
                //get the audio stream
                AmazonPolly.setText(textArea.getText());
                AmazonPolly.setSpeed(Integer.toString(speedBar.getValue()));

                if (AmazonPolly.getSpeed().indexOf('%') == -1)
                {
                    AmazonPolly.setSpeed(AmazonPolly.getSpeed() + "%");
                }

                InputStream speechStream = null;
                try
                {

                    speechStream = amazonPolly.synthesize("<speak><prosody rate='" + AmazonPolly.getSpeed() + "'>" + AmazonPolly.getText() + "</prosody></speak>", OutputFormat.Mp3);
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }


                AWSCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();

                //create an MP3 player
                AdvancedPlayer player = null;

                if (speechStream != null)
                {
                    try
                    {
                        player = new AdvancedPlayer(speechStream, javazoom.jl.player.FactoryRegistry.systemRegistry().createAudioDevice());
                    }
                    catch (JavaLayerException e1)
                    {
                        e1.printStackTrace();
                    }

                    player.setPlayBackListener(new PlaybackListener()
                    {
                        // For testing purposes only.
                        @Override
                        public void playbackStarted(PlaybackEvent evt)
                        {
                            System.out.println("---------------------------------------------------------\n");
                            System.out.println(AmazonPolly.getText());
                        }

                        @Override
                        public void playbackFinished(PlaybackEvent evt)
                        {
                            System.out.println("\n---------------------------------------------------------\n");
                        }
                    });


                    // play it!
                    try
                    {
                        player.play();
                    }
                    catch (JavaLayerException e1)
                    {
                        e1.printStackTrace();
                    }
                }
                else if (value.equals(removeLineBreaksString))
                {
                    AmazonPolly.setText(textArea.getText());
                    textArea.setText(AmazonPolly.getText().replaceAll("\\r\\n|\\r|\\n", " "));
                }

                textArea.requestFocusInWindow();
            }
        }
    }

    /**
     * This method clears the dialog and hides it.
     */
    public void exit()
    {
        dispose();
    }

    public static void main(String... args)
    {
        //create JDialog and components on EDT
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                new CustomDialog(null, "David").setVisible(true);
            }
        });
    }
}