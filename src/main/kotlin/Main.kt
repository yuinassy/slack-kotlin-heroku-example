import com.slack.api.bolt.App
import com.slack.api.bolt.AppConfig
import com.slack.api.bolt.jetty.SlackAppServer
import com.slack.api.bolt.response.Response
import io.github.cdimascio.dotenv.dotenv

fun main(args: Array<String>) {
    val dotenv = dotenv {
        ignoreIfMissing = true
    }

    val appConfig = AppConfig.builder()
        .singleTeamBotToken(dotenv["SLACK_BOT_TOKEN"])
        .signingSecret(dotenv["SLACK_SIGNING_SECRET"])
        .build()

    val app = App(appConfig)

    app.command("/arigato"){ req, ctx ->
        ctx.ack("どういたしまして")
    }

    app.command("/hello") { req, ctx ->
        ctx.ack(":wave: Hello!")
    }

    app.command("/meeting") { req, ctx ->
        val res = ctx.client().viewsOpen {
            it.triggerId(ctx.triggerId).view(buildView())
        }
        if (res.isOk) ctx.ack()
        else Response.builder().statusCode(500).body(res.error).build()
    }

    app.blockAction(Const.Action.categorySelection) { req, ctx ->
        val categoryId = req.payload.actions[0].selectedOption.value
        val currentView = req.payload.view
        val privateMetadata = currentView.privateMetadata
        val viewForTheCategory = buildViewByCategory(categoryId, privateMetadata)
        val viewsUpdateResp = ctx.client().viewsUpdate {
            it
                .viewId(currentView.id)
                .hash(currentView.hash)
                .view(viewForTheCategory)
        }
        ctx.ack()
    }

    app.viewSubmission(Const.CallbackId.meetingManagement) { req, ctx ->
        val privateMetadata = req.payload.view.privateMetadata
        val stateValues = req.payload.view.state.values
        val agenda = stateValues["agenda-block"]!!["agenda-action"]!!.value
        val errors = mutableMapOf<String, String>()
        if (agenda.length <= 10) {
            errors["agenda-block"] = "Agenda needs to be longer than 10 characters."
        }
        if (errors.isNotEmpty()) {
            ctx.ack { it.responseAction("errors").errors(errors) }
        } else {
            // TODO: may store the stateValues and privateMetadata
            // Responding with an empty body means closing the modal now.
            // If your app has next steps, respond with other response_action and a modal view.
            ctx.ack()
        }
    }

    // when a user clicks "Cancel"
    // "notify_on_close": true is required
    app.viewClosed(Const.CallbackId.meetingManagement) { req, ctx ->
        // Do some cleanup tasks
        ctx.ack()
    }

    val port = dotenv["PORT"].toIntOrNull() ?: 3000 // On Heroku, you need to use PORT env variable.
    val server = SlackAppServer(app, port)
    server.start() // http://localhost:3000/slack/events
}
