import com.slack.api.model.kotlin_extension.view.blocks
import com.slack.api.model.view.View
import com.slack.api.model.view.Views.*
import com.slack.api.util.json.GsonFactory


fun buildView(): View {
    return view { thisView ->
        thisView
            .callbackId(Const.CallbackId.meetingManagement)
            .type("modal")
            .notifyOnClose(true)
            .title(viewTitle { it.type("plain_text").text("Meeting Arrangement").emoji(true) })
            .submit(viewSubmit { it.type("plain_text").text("Submit").emoji(true) })
            .close(viewClose { it.type("plain_text").text("Cancel").emoji(true) })
            .privateMetadata("""{"someData":"someValue"}""")
            .blocks {
                section {
                    blockId("category-block")
                    markdownText("Select a category of the meeting!")
                    staticSelect {
                        actionId(Const.Action.categorySelection)
                        placeholder("Select a category")
                        options {
                            option {
                                text("plain_text", "Customer")
                                value("customer")
                            }
                            option {
                                text("plain_text", "Partner")
                                value("partner")
                            }
                            option {
                                text("plain_text", "Internal")
                                value("internal")
                            }
                        }
                    }
                }
                input {
                    blockId("agenda-block")
                    plainTextInput {
                        actionId("agenda-action")
                        multiline(true)
                    }
                    label("Detailed Agenda", emoji = true)
                }
            }
    }
}

data class PrivateMetadata(var categoryId: String?)

fun buildViewByCategory(categoryId: String, privateMetadata: String?): View? {
    val gson = GsonFactory.createSnakeCase()
    val metadata: PrivateMetadata = gson.fromJson(
        privateMetadata,
        PrivateMetadata::class.java
    )
    metadata.categoryId = categoryId
    val updatedPrivateMetadata = gson.toJson(metadata)
    return view { thisView ->
        thisView
            .callbackId(Const.CallbackId.meetingManagement)
            .type("modal")
            .notifyOnClose(true)
            .title(viewTitle { it.type("plain_text").text("Meeting Arrangement").emoji(true) })
            .submit(viewSubmit { it.type("plain_text").text("Submit").emoji(true) })
            .close(viewClose { it.type("plain_text").text("Cancel").emoji(true) })
            .privateMetadata(updatedPrivateMetadata)
            .blocks {
                section {
                    blockId("category-block")
                    markdownText("You've selected \"$categoryId\"")
                }
                input {
                    blockId("agenda-block")
                    plainTextInput {
                        actionId("agenda-action")
                        multiline(true)
                    }
                    label("Detailed Agenda", emoji = true)
                }
            }
    }
}
