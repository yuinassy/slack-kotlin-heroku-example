# slack-kotlin-heroku-example

Slack app example for Heroku deployment, written in Kotlin, using Bolt framework.

You need to configure [your Slack app](https://api.slack.com/apps) to run this as backend.
Follow [the official guide](https://slack.dev/java-slack-sdk/guides/getting-started-with-bolt) for that.

This example hosts two commands. `/hello` command is from [this guide](https://slack.dev/java-slack-sdk/guides/getting-started-with-bolt),
and `/meeting` command is from [this guide](https://slack.dev/java-slack-sdk/guides/modals).

## Run locally

To run this locally, create `.env` file on the project root, with following content.

```
PORT=3000
SLACK_BOT_TOKEN=<your_bot_token>
SLACK_SIGNING_SECRET=<your_signing_secret>
```

Replace `<your_bot_token>` and `<your_signing_secret>` with yours.

Then run.

```bash
gradlew run # Or from IntelliJ IDEA "Run" button.
```

Note that to actually trigger this from your Slack workspace, you need to expose this on the Internet.
You can [use ngrok](https://slack.dev/node-slack-sdk/tutorials/local-development) or something for that.

## Heroku deployment

Follow [this guide](https://devcenter.heroku.com/articles/getting-started-with-gradle-on-heroku) to set up.

Once you've created your Heroku app with `heroku create`, you can deploy

```bash
git push heroku master
```

and configure environment variables.

```bash
heroku config:set SLACK_BOT_TOKEN="<your_bot_token>"
heroku config:set SLACK_SIGNING_SECRET="<your_signing_secret>"
```

Using the Heroku hosting URL (shown as the output of `git push heroku master`),
you need to replace the following settings on [your Slack app configuration](https://api.slack.com/apps).

* `Features` > `Interactivity & Shortcuts` > `Request URL`
* `Features` > `Slash Commands` > `/hello` > `Request URL`
* `Features` > `Slash Commands` > `/meeting` > `Request URL`

All of them should be set to `https://<your_heroku_app_id>.herokuapp.com/slack/events`.

Once reinstalled, you can run those commands from your Slack workspace!
