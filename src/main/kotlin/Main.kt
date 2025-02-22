import `object`.ArigatoPrivateMetadata
import `object`.ClapActionValue
import com.slack.api.bolt.App
import com.slack.api.bolt.AppConfig
import com.slack.api.bolt.jetty.SlackAppServer
import com.slack.api.bolt.response.Response
import com.slack.api.methods.kotlin_extension.request.chat.blocks
import com.slack.api.methods.request.users.profile.UsersProfileGetRequest
import com.slack.api.methods.response.chat.ChatPostMessageResponse
import com.slack.api.methods.response.chat.ChatUpdateResponse
import com.slack.api.model.block.Blocks.actions
import com.slack.api.model.block.Blocks.asBlocks
import com.slack.api.util.json.GsonFactory
import db.DatabaseFactory
import io.github.cdimascio.dotenv.dotenv
import mu.KotlinLogging
import java.util.*

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    val dotenv = dotenv {
        ignoreIfMissing = true
    }

    val jdbcUrl = dotenv["JDBC_DATABASE_URL"]
    DatabaseFactory.init(jdbcUrl)

    val appConfig = AppConfig.builder()
        .singleTeamBotToken(dotenv["SLACK_BOT_TOKEN"])
        .signingSecret(dotenv["SLACK_SIGNING_SECRET"])
        .build()

    val app = App(appConfig)

    app.command("/arigato") { req, ctx ->
        val res = ctx.client().viewsOpen {
            it.triggerId(ctx.triggerId).view(buildArigatoView(ctx.channelId))
        }
        if (res.isOk) ctx.ack()
        else Response.builder().statusCode(500).body(res.error).build()
    }

    app.viewSubmission(Const.CallbackId.arigato) { req, ctx ->
        val privateMetadataString = req.payload.view.privateMetadata
        val gson = GsonFactory.createSnakeCase()
        val privateMetadata: ArigatoPrivateMetadata = gson.fromJson(
            privateMetadataString,
            ArigatoPrivateMetadata::class.java
        )
        val stateValues = req.payload.view.state.values
        val message = stateValues["message-block"]!!["message-action"]!!.value
        val point = stateValues["point-block"]!!["point-action"]!!.value?.toIntOrNull()
        val selectedUserId = stateValues["to-select-block"]!!["to-select-action"]!!.selectedUser
        printLog("selectedUserId=${selectedUserId}")
        printLog("point=${point}")

        val errors = mutableMapOf<String, String>()
        if (message.length <= 10) {
            errors["message-block"] = "Agenda needs to be longer than 10 characters."
        }
        if (errors.isNotEmpty()) {
            ctx.ack { it.responseAction("errors").errors(errors) }
        } else {
            val resp = ctx.client().usersProfileGet(
                UsersProfileGetRequest.builder()
                    .token(ctx.botToken)
                    .user(ctx.requestUserId)
                    .build()
            )
            printLog("channelId=${privateMetadata.channelId}")

            val postId = UUID.randomUUID()

            val result: ChatPostMessageResponse? = ctx.client().chatPostMessage { r ->
                r.channel(privateMetadata.channelId)
                    .username(resp.profile?.displayName ?: "名無し")
                    .iconUrl(resp.profile?.image192)
//                    .text("<@${selectedUserId}>\n${message}")
                    .blocks {
                        buildArigatoChat(postId, selectedUserId, message, 0)
//                        section {
//                            markdownText("<@${selectedUserId}>\n${message}")
//                        }
//                        actions {
//                            // JSON の構造と揃えるなら、ここに elements { } のブロックを置くこともできますが、省略しても構いません
//                            // これは section ブロックの accessory についても同様です
//                            button {
//                                text(":clap:×1", emoji = true)
//                                value("clap1")
//                                actionId(Const.Action.clap1)
//                            }
//                            button {
//                                text(":clap:×3", emoji = true)
//                                value("clap3")
//                                actionId(Const.Action.clap3)
//                            }
//                            button {
//                                text(":clap:×5", emoji = true)
//                                value("clap5")
//                                actionId(Const.Action.clap5)
//                            }
//                        }
                    }
            }

            // TODO: may store the stateValues and privateMetadata
            // Responding with an empty body means closing the modal now.
            // If your app has next steps, respond with other response_action and a modal view.
            ctx.ack()
        }
    }

    app.blockAction(Const.Action.clap1) { req, ctx ->
        printLog("blockAction: ${Const.Action.clap1}")
        ctx.ack()
    }

    app.blockAction(Const.Action.clap3) { req, ctx ->
        printLog("blockAction: ${Const.Action.clap3}")

//        val stateValues = req.payload.view.state.values
//        printLog("block-id: ${stateValues["block-id"]!!.}")

        val gson = GsonFactory.createSnakeCase()
        val clapActionValue = gson.fromJson(req.payload.actions[0]?.value, ClapActionValue::class.java)

        val chatUpdateResponse: ChatUpdateResponse? = ctx.client().chatUpdate { r ->
            r.channel(req.payload.channel.id)
                .ts(req.payload.message.ts) // タイムスタンプでメッセージを判別してるっぽい。
                .blocks {
                    section {
                        markdownText("CLAP3!!!: postId=${clapActionValue?.postId}, type=${clapActionValue?.type}")
                    }
                }
        }
        printLog("chatUpdateResponse=$chatUpdateResponse")

        ctx.ack()
    }

    app.blockAction(Const.Action.clap5) { req, ctx ->
        printLog("blockAction: ${Const.Action.clap5}")
        printLog(req.payload.message.ts)
        printLog(req.payload.channel.id)

        ctx.ack()
    }

    app.command("/hello") { req, ctx ->
        printLog("requestUserId=${ctx.requestUserId}")
        printLog("requestUserToken=${ctx.requestUserToken}")
        printLog("botId=${ctx.botId}")
        printLog("botToken=${ctx.botToken}")

//        val resp = ctx.client().usersIdentity(
//            UsersIdentityRequest.builder().token("xoxp-1948231796678-2830443268582-2991803022945-dccb636ab86f7608a3b97dbd701bc58a").build())

//        printLog("user.id = ${resp.user?.id}")
//        printLog("user.name = ${resp.user?.name}")
//        printLog("user.email = ${resp.user?.email}")
//        printLog("user.image32 = ${resp.user?.image32}")
//        printLog("error = ${resp.error}")
//        printLog("userToken = ${ctx.requestUserToken}")

        val respBot = ctx.client().usersProfileGet(
            UsersProfileGetRequest.builder()
                .token(ctx.botToken)
                .user(ctx.requestUserId)
                .build()
        )
        printLog("respBot.isOk = ${respBot.isOk}")
        printLog("respBot.error = ${respBot.error}")
        printLog("respBot.profile.botId = ${respBot.profile?.botId}")
        printLog("respBot.profile.displayName = ${respBot.profile?.displayName}")
        printLog("respBot.profile.email = ${respBot.profile?.email}")
        printLog("respBot.profile.image24 = ${respBot.profile?.image24}")
        printLog("respBot.profile.image192 = ${respBot.profile?.image192}")
        printLog("respBot.profile.realName = ${respBot.profile?.realName}")

//        val respUser = ctx.client().usersProfileGet(
//            UsersProfileGetRequest.builder()
//                .token("xoxp-1948231796678-2830443268582-2991803022945-dccb636ab86f7608a3b97dbd701bc58a").build()
//        )
//        printLog("respUser.isOk = ${respUser.isOk}")
//        printLog("respUser.error = ${respUser.error}")
//        printLog("respUser.profile.botId  = ${respUser.profile?.botId}")
//        printLog("respUser.profile.displayName = ${respUser.profile?.displayName}")
//        printLog("respUser.profile.email = ${respUser.profile?.email}")
//        printLog("respUser.profile.image24 = ${respUser.profile?.image24}")
//        printLog("respUser.profile.realName = ${respUser.profile?.realName}")

        val result: ChatPostMessageResponse? = ctx.client().chatPostMessage { r ->
            r // The token you used to initialize your app is stored in the `context` object
                .channel(ctx.channelId)
                .username(respBot.profile?.displayName ?: "名無し")
                .iconUrl(respBot.profile.image192) // TODO アイコンがなぜかうまくいかない。
//                .iconEmoji("clap")
                .blocks {
                    section {
                        markdownText("world!!")
                        image(respBot.profile.image192, altText = "user icon")
                    }
                }
        }

        ctx.ack()
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
