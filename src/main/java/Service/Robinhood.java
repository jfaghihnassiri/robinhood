package Service;

import Model.Account;
import Model.Fundamentals;
import Model.Instrument;
import Model.Order;
import Model.Position;
import Model.Quote;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;

public class Robinhood {
    //
    private static Robinhood robinhood = null; // self reference
    private static final String TOKEN = "Token ";
    private final HttpClient httpclient;
    private String token = null;
    private String username = null;

    // static urls
    private static final String TOP_MOVERS = "https://api.robinhood.com/midlands/tags/tag/top-movers/";
    private static final String POSITIONS = "https://api.robinhood.com/positions/";
    private static final String TOKEN_AUTH = "https://api.robinhood.com/api-token-auth/";
    private static final String ACCOUNTS = "https://api.robinhood.com/accounts/";
    private static final String ORDER = "https://api.robinhood.com/orders/";

    // constructor
    private Robinhood() {
        httpclient = HttpClients.createDefault();
    }

    // singleton getter
    public static Robinhood getInstance() {
        if (robinhood == null) {
            robinhood = new Robinhood();
        }
        return robinhood;
    }

    /**
     * public API
    **/

    public ArrayList<Instrument> getTopMoverInstruments() {
        JSONArray topMoversArray = robinhood.getTopMovers().getJSONArray("instruments");
        ArrayList<Instrument> instruments = new ArrayList<>();
        for (int i = 0; i < topMoversArray.length(); i++) {
            instruments.add(robinhood.getInstrument(topMoversArray.getString(i)));
        }
        instruments.sort(Comparator.comparing(Instrument::getPercentChangeToday));
        return instruments;
    }

    public Instrument getInstrument(String url) {
        Instrument i = new Instrument(get(url));
        i.setQuoteObj(getQuote(i.getQuote()));
        i.setFundamentalsObj(getFundamentals(i.getFundamentals()));
        return i;
    }

    public Quote getQuote(String url) {
        return new Quote(get(url));
    }

    public Fundamentals getFundamentals(String url) {
        return new Fundamentals(get(url));
    }

    public ArrayList<Position> getPositons() {
        ArrayList<Position> positions = new ArrayList<>();
        JSONObject response = getAuthenticated(POSITIONS);
        JSONArray results = response.getJSONArray("results");
        for (int i = 0; i < results.length(); i++) {
            JSONObject result = results.getJSONObject(i);
            if (result.has("quantity") && result.getDouble("quantity") != 0.0 && result.has("instrument")) {
                Position position = new Position(result);
                position.setInstrumentObj(getInstrument(position.getInstrument()));
                positions.add(position);
            }
        }
        return positions;
    }

    public ArrayList<Account> getAccounts() {
        ArrayList<Account> accounts = new ArrayList<>();
        JSONObject response = getAuthenticated(ACCOUNTS);
        JSONArray results = response.getJSONArray("results");
        for (int i = 0; i < results.length(); i++) {
            JSONObject result = results.getJSONObject(i);
            Account account = new Account(result);
            accounts.add(account);
        }
        return accounts;
    }

    public JSONObject sendOrder(Order order) {
        return postAuthenticated(ORDER, order.toString());
    }

    /**
     * internal methods
     **/

    private Header getAuthHeader() {
        if (token == null) {
            getNewToken();
        }
        return new BasicHeader("Authorization", TOKEN+token);
    }

    private Header getAcceptJSONHeader() {
        return new BasicHeader("Accept", "application/json");
    }

    private void getNewToken() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Username: ");
            username = br.readLine();
            System.out.print("Password: ");
            String password = br.readLine();
            JSONObject response = post(TOKEN_AUTH, username, password);
            token = response.getString("token");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONObject getTopMovers() {
        return get(TOP_MOVERS);
    }

    private JSONObject getAuthenticated(String url) {
        return get(url, getAuthHeader());
    }

    private JSONObject get(String url, Header... headers) {
        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeaders(headers);
            HttpResponse response = httpclient.execute(httpGet);
            return handleResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONObject post(String url, String username, String password) {
        try {
            HttpPost httpPost = new HttpPost(url);

            ArrayList<NameValuePair> postParameters = new ArrayList<>();
            postParameters.add(new BasicNameValuePair("username", username));
            postParameters.add(new BasicNameValuePair("password", password));

            httpPost.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));

            HttpResponse response = httpclient.execute(httpPost);
            return handleResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONObject postAuthenticated(String url, String s) {
        return post(url, s, getAuthHeader(), getAcceptJSONHeader(), new BasicHeader("Content-Type", "application/json"));
    }

    private JSONObject post(String url, String s, Header... headers) {
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(s));
            httpPost.setHeaders(headers);
            HttpResponse response = httpclient.execute(httpPost);
            return handleResponse(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private JSONObject handleResponse(HttpResponse response) throws IOException {
        int status = response.getStatusLine().getStatusCode();
        HttpEntity entity = response.getEntity();
        if (entity == null) return null;
        String body = EntityUtils.toString(entity);
        if (status >= 200 && status< 300) {
            return body != null ? new JSONObject(body) : null;
        } else {
            throw new ClientProtocolException("Unexpected response status: " + status + "\n" + body);
        }
    }

}
