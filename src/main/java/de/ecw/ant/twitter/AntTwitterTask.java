package de.ecw.ant.twitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterException;
import twitter4j.conf.ConfigurationBuilder;

import com.rosaloves.net.shorturl.bitly.Bitly;
import com.rosaloves.net.shorturl.bitly.BitlyFactory;

/**
 * AntTwitterTask is a very simple Ant task for triggering Twitter from Ant.
 * Complete code is licensed under Apache License.
 * 
 * <code>
 * <?xml version="1.0"?>
 * 
 * <project name="TwitterTestTask" default="main" basedir=".">
 *  <taskdef name="twitter" classname="de.ecw.ant.twitter.AntTwitterTask"/>
 * 
 *  <target name="main">
 *    <twitter message="Hello World from ant-twitter" username="<your-twitter-username>" password="<your-twitter-password>" bitlyUsername="<your-bitly-username>" bitlyApiKey="<your-bitley-key>" enableBitly="true" />
 *  </target>
 * </project>
* </code>
 * 
 * @author Christopher Klein &lt;christopher[dot]klein[at]ecw[dot]de&gt; -
 *         http://wap.ecw.de
 * @license Apache License
 * @version @VERSION@
 */
public class AntTwitterTask extends Task
{
    /**
     * Not sure, how Ant handles boolean values in Ant scripts
     */
    public final static String TRUE = "true";

    /**
     * Not sure, how Ant handles boolean values in Ant scripts
     */
    public final static String FALSE = "false";

    /**
     * Regular expression pattern for URLs.<br />
     * thanks to Anirudha Mathur
     * 
     * @link 
     *       http://www.geekzilla.co.uk/View2D3B0109-C1B2-4B4E-BFFD-E8088CBC85FD.
     *       htm
     */
    public final static String URL_REGEXP_PATTERN = "((https?|ftp|gopher|telnet|file|notes|ms-help):((//)|(\\\\))[\\w\\d:#%/;$()~_?\\-=\\\\.&]*)";

    /**
     * Agent name
     */
    public final static String AGENT_NAME = "ant-twitter";

    /**
     * Username for bit.ly - a sample API key/username combination can be found
     * on
     * 
     * @link http://code.google.com/p/bitlyj/
     */
    private String bitlyUsername;

    /**
     * API key for bit.ly - a sample API key can be found on
     * 
     * @link http://code.google.com/p/bitlyj/
     */
    private String bitlyApiKey;

    /**
     * Enable Bit.ly shortening service
     */
    private String enableBitly = FALSE;

    /**
     * Consumer Key for Twitter
     */
    private String consumerKey;

    /**
     * Consumer Secret for Twitter
     */
    private String consumerSecret;

    /**
     * accessToken for Twitter
     */
    private String accessToken;
    
    /**
     * accessToken secret for Twitter
     */
    private String accessTokenSecret;
    
    /**
     * Agent name from which the message is sent
     */
    private String agentName = AGENT_NAME;

    /**
     * Status update
     */
    private String message;

    /**
     * Get Twitter consumer key
     * 
     * @return
     */
    public String getConsumerKey()
    {
        return consumerKey;
    }

    /**
     * Set Twitter consumer key
     * 
     * @param key
     */
    public void setConsumerKey(String key)
    {
        this.consumerKey = key;
    }

    /**
     * Get Twitter consumer secret
     * 
     * @return
     */
    public String getConsumerSecret()
    {
        return consumerSecret;
    }

    /**
     * Set Twitter consumer secret
     * 
     * @param secret
     */
    public void setConsumerSecret(String secret)
    {
        this.consumerSecret = secret;
    }
    /**
     * Get Twitter access token
     * 
     * @return
     */
    public String getAccessToken()
    {
    	return accessToken;
    }
    
    /**
     * Set Twitter accesstoken
     * 
     * @param key
     */
    public void setAccessToken(String token)
    {
    	this.accessToken = token;
    }
    
    /**
     * Get Twitter access token secret
     * 
     * @return
     */
    public String getAccessTokenSecret()
    {
    	return accessTokenSecret;
    }
    
    /**
     * Set Twitter consumer secret
     * 
     * @param secret
     */
    public void setAccessTokenSecret(String secret)
    {
    	this.accessTokenSecret = secret;
    }

    /**
     * Get tweet message
     * 
     * @return
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Set tweet message
     * 
     * @param message
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * Set Bit.ly API key
     * 
     * @param bitlyApiKey
     */
    public void setBitlyApiKey(String bitlyApiKey)
    {
        this.bitlyApiKey = bitlyApiKey;
    }

    /**
     * Get Bit.ly API key
     * 
     * @return
     */
    public String getBitlyApiKey()
    {
        return bitlyApiKey;
    }

    /**
     * Set Bit.ly username
     * 
     * @param bitlyUsername
     */
    public void setBitlyUsername(String bitlyUsername)
    {
        this.bitlyUsername = bitlyUsername;
    }

    /**
     * Get Bit.ly username
     * 
     * @return
     */
    public String getBitlyUsername()
    {
        return bitlyUsername;
    }

    /**
     * Enable bit.ly support - default is set to false
     * 
     * @param enableBitly
     */
    public void setEnableBitly(String enableBitly)
    {
        this.enableBitly = enableBitly;
    }

    /**
     * Is bit.ly support enabled - default is set to false
     * 
     * @return
     */
    public String getEnableBitly()
    {
        return enableBitly;
    }

    /**
     * Set agent name from which client the tweet was sent
     * 
     * @param agentName
     */
    public void setAgentName(String agentName)
    {
        this.agentName = agentName;
    }

    /**
     * Get agent name from which client the tweet was sent
     */
    public String getAgentName()
    {
        return agentName;
    }

    /**
     * Executes Ant task:
     * <ul>
     * <li>establish a connection to Twitter via twitter4j</li>
     * <li>if enabled, execute URL shortening via bitly</li>
     * <li>check the input message and split it into chunks if message is larger
     * than 140 characters</li>
     * <li>update status of every message</li>
     * </ul>
     */
    public void execute() throws BuildException
    {
        String useMessage = getMessage();

        // validate Twitter parameters
        if ((getConsumerKey() == null)
                        || ((getConsumerKey() != null) && (getConsumerKey().length() == 0))
                        || (getConsumerSecret() == null)
                        || ((getConsumerSecret() != null) && (getConsumerSecret().length() == 0)))
        {
            log(
                            "You have to enter a valid Twitter username/password combination, missing arguments!",
                            Project.MSG_ERR);
            return;
        }

        // should URLs be shortened?
        if ((getEnableBitly() != null)
                        && (getEnableBitly().toLowerCase().equals(TRUE)))
        {
            // validate bit.ly parameters
            if ((getBitlyUsername() == null)
                            || ((getBitlyUsername() != null) && (getBitlyUsername()
                                            .length() == 0))
                            || (getBitlyApiKey() == null)
                            || ((getBitlyApiKey() != null) && (getBitlyApiKey()
                                            .length() == 0)))
            {
                log(
                                "You have enabled bit.ly support, but bit.ly username or API key is missing - bit.ly support is disabled",
                                Project.MSG_WARN);
            }
            else
            {
                useMessage = shortenUrls(message);
            }
        }

        // new Twitter client
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
          .setOAuthConsumerKey(getConsumerKey())
          .setOAuthConsumerSecret(getConsumerSecret())
          .setOAuthAccessToken(getAccessToken())
          .setOAuthAccessTokenSecret(getAccessTokenSecret());
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        
        // assume that all messages are longer than 140 chars
        List<String> messages = AntTwitterTask.splitMessage(useMessage);

        log("Twittering update (" + messages.size() + " tweets)",
                        Project.MSG_INFO);

        int iTotalPosts = messages.size();
        int iSuccededUpdates = 0;

        try
        {
            // post every tweet
            for (int i = 0; i < iTotalPosts; i++)
            {
                String msg = messages.get(i);
                log(msg.length() + " chars: " + msg, Project.MSG_INFO);
                twitter.updateStatus(msg);
                iSuccededUpdates++;
            }
        }
        catch (TwitterException e)
        {
            log("Failed to update Twitter status", e, Project.MSG_ERR);
        }

        log("Tweets posted: " + iSuccededUpdates + "/" + iTotalPosts,
                        Project.MSG_INFO);
    }

    /**
     * Taken from jtwitter. Split a long message up into shorter chunks
     * 
     * @param _msg
     * @return longStatus broken into a list of max 140 char strings
     */
    public final static List<String> splitMessage(String _msg)
    {
        // Is it really long?
        if (_msg.length() <= 140)
            return Collections.singletonList(_msg);
        // Multiple tweets for a longer post
        List<String> sections = new ArrayList<String>(4);
        StringBuilder tweet = new StringBuilder(140);
        String[] words = _msg.split("\\s+");
        for (String w : words)
        {
            // messages have a max length of 140
            // plus the last bit of a long tweet tends to be hidden on
            // twitter.com, so best to chop 'em short too
            if (tweet.length() + w.length() + 1 > 140)
            {
                // Emit
                // removed ... code - it can lead to more than 140 chars in a
                // message
                sections.add(tweet.toString());
                tweet = new StringBuilder(140);
                tweet.append(w);
            }
            else
            {
                if (tweet.length() != 0)
                    tweet.append(" ");
                tweet.append(w);
            }
        }
        // Final bit
        if (tweet.length() != 0)
            sections.add(tweet.toString());
        return sections;
    }

    /**
     * Does a shortening of all URLs in a string via bit.ly
     * 
     * @param _message
     * @return
     */
    protected String shortenUrls(String _message)
    {
        Pattern p = Pattern.compile(URL_REGEXP_PATTERN);
        Matcher matcher = p.matcher(_message);

        ArrayList<String> urls = new ArrayList<String>();

        // iterate over every match and put URL to ArrayList
        while (matcher.find())
        {
            urls.add(_message.substring(matcher.start(), matcher.end()));
        }

        // some URLs were found in _message
        if (urls.size() > 0)
        {
            // create new instance for bit.ly
            Bitly bitly = BitlyFactory.newInstance(bitlyUsername, bitlyApiKey);

            // iterate over all URLs and shorten them
            for (int i = 0, m = urls.size(); i < m; i++)
            {
                String url = urls.get(i);

                try
                {
                    String shortUrl = bitly.shorten(url).getShortUrl()
                                    .toString();
                    _message = _message.replaceAll(url, shortUrl);

                }
                catch (Exception e)
                {
                    log("Failed to shorten URL '" + url + "' via bit.ly", e,
                                    Project.MSG_ERR);
                }
            } // iterate URLs
        } // if urls.size() > 0

        return _message;
    }
}
