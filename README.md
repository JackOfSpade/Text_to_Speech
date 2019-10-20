# Demo 
<img src="https://i.imgur.com/zoRFvVH.jpg" alt="alt text" width="850" height="381">

![alt text](https://i.imgur.com/AizqmRg.png)

This interface bridges the gap between common users and the Amazon Polly API (featuring the most natural sounding voices available).

Amazon Polly currently does not provide a user-friendly software for its API.

# To Use
Download and run `TextToSpeech.jar`

# For Developers
Due to security reason, an API key for Amazon Polly is not included in the source code. You must change the following line in `AmazonPolly.java` before compiling:
```java
BasicAWSCredentials credentials = new BasicAWSCredentials("Your_Access_Key_ID", "Your_Secret_Key");
```
