import `object`.ArigatoPrivateMetadata
import com.slack.api.model.kotlin_extension.view.blocks
import com.slack.api.model.view.View
import com.slack.api.model.view.Views
import com.slack.api.util.json.GsonFactory

fun buildArigatoView(channelId: String): View {
    val privateMetadata = ArigatoPrivateMetadata(channelId)
    val gson = GsonFactory.createSnakeCase()
    val privateMetadataString = gson.toJson(privateMetadata)
    return Views.view { thisView ->
        thisView
            .callbackId(Const.CallbackId.arigato)
            .type("modal")
            .notifyOnClose(true)
            .title(Views.viewTitle { it.type("plain_text").text("Arigato You").emoji(true) })
            .submit(Views.viewSubmit { it.type("plain_text").text("OK").emoji(true) })
            .close(Views.viewClose { it.type("plain_text").text("キャンセル").emoji(true) })
            .privateMetadata(privateMetadataString)
            .blocks {
                input {
                    blockId("to-select-block")
                    label("To")
                    usersSelect {
                        placeholder("佐藤")
                        actionId("to-select-action")
                    }
                }

                input {
                    blockId("point-block")
                    plainTextInput {
                        actionId("point-action")
                        multiline(false)
                        placeholder("39")
                    }
                    label("ポイント", emoji = true)
                }


                input {
                    blockId("message-block")
                    plainTextInput {
                        actionId("message-action")
                        multiline(true)
                    }
                    label("メッセージ本文", emoji = true)
                }
            }
    }
}


