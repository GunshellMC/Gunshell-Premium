package com.jazzkuh.gunshell.utils.license;

import com.jazzkuh.gunshell.utils.PluginUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class LicenseVerification {

    private final URI host;
    private final String licenseKey;
    private final String version;
    private final String apiKey;

    public PremiumResult check() {
        if (licenseKey.equalsIgnoreCase("Enter your license key")) {
            PremiumResult result = new PremiumResult();
            result.setStatusMsg("INVALID_LICENSEKEY");
            result.setStatusOverview("failed");
            result.setStatusCode(401);
            return result;
        }

        HttpPost post = new HttpPost(host);

        // add request parameter, form parameters
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("licensekey", licenseKey));
        urlParameters.add(new BasicNameValuePair("version", version));
        urlParameters.add(new BasicNameValuePair("hwid", PluginUtils.getHardwareId()));

        try {
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        post.setHeader("Authorization", apiKey);

        try (CloseableHttpClient httpClient = HttpClients.createDefault(); CloseableHttpResponse response = httpClient.execute(post)) {
            String data = EntityUtils.toString(response.getEntity());
            JSONObject obj = new JSONObject(data);
            if (!obj.has("status_msg") || !obj.has("status_code") || !obj.has("status_overview"))
                return new PremiumResult();

            PremiumResult result = new PremiumResult();
            if (obj.has("product")) {
                result.setProduct(obj.getString("product"));
                result.setVersion(obj.getString("version"));
                result.setClientName(obj.getString("clientname"));
                result.setDiscordUsername(obj.getString("discord_username"));
                result.setDiscordId(obj.getString("discord_id"));
                result.setDiscordTag(obj.getString("discord_tag"));
                result.setStatusId(obj.getString("status_id"));
            }

            result.setStatusMsg(obj.getString("status_msg"));
            result.setStatusOverview(obj.getString("status_overview"));
            result.setStatusCode(obj.getInt("status_code"));

            return result;

        } catch (IOException e) {
            e.printStackTrace();
            return new PremiumResult();
        }
    }
}