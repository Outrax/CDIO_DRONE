package de.yadrone.apps.cdio.gui;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;

class ConsoleOutputStream extends OutputStream
{
    private final JTextArea     textarea;
    private final StringBuilder builder = new StringBuilder();

    ConsoleOutputStream(JTextArea textarea)
    {
        this.textarea = textarea;
    }

    @Override
    public void flush() {}

    @Override
    public void close() {}

    @Override
    public void write(int character) throws IOException
    {
        if(character == '\n')
        {
            final String text = "> " + this.builder.toString() + "\n";

            SwingUtilities.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    textarea.append(text);
                }
            });

            this.builder.setLength(0);

            return;
        }

        builder.append((char) character);
    }
}