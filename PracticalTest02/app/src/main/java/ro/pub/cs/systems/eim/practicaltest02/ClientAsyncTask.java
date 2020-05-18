package ro.pub.cs.systems.eim.practicaltest02;


import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientAsyncTask extends AsyncTask<String, String, Void> {
    TextView response;

    public ClientAsyncTask(TextView response) {
        this.response = response;
    }
    @Override
    protected Void doInBackground(String... strings) {
        try {
            String address = strings[0],
                    port = strings[1],
                    currency = strings[2];

            if (!currency.equals(Constants.USD) && !currency.equals(Constants.EUR)) {
                Log.d(Constants.DEBUG, Constants.INVALID_CURRENCY);
                publishProgress(Constants.INVALID_CURRENCY);
                return null;
            }

            Socket socket = new Socket(address, Integer.valueOf(port));
            PrintWriter writer = Utilities.getWriter(socket);
            BufferedReader reader = Utilities.getReader(socket);

            writer.println(currency);
            writer.flush();

            String value = reader.readLine();
            publishProgress(value);

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onProgressUpdate(String... result) {
        response.setText(result[0]);
    }

    protected void onPostExecute(Bitmap result) {
    }
}
