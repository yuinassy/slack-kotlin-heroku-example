import `object`.ClapActionValue
import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import com.slack.api.util.json.GsonFactory
import java.util.*

fun LayoutBlockDsl.buildArigatoChat(postId: UUID, selectedUserId: String, message: String, nClaps: Int): Unit {
    val gson = GsonFactory.createSnakeCase()

    section {
        markdownText("<@${selectedUserId}>\n${message}")
        blockId("block-id")
    }
    actions {
        // JSON の構造と揃えるなら、ここに elements { } のブロックを置くこともできますが、省略しても構いません
        // これは section ブロックの accessory についても同様です
        button {
            text(":clap:×1", emoji = true)
            value(gson.toJson(ClapActionValue(postId, "clap1")))
            actionId(Const.Action.clap1)
        }
        button {
            text(":clap:×3", emoji = true)
            value(gson.toJson(ClapActionValue(postId, "clap3")))
            actionId(Const.Action.clap3)
        }
        button {
            text(":clap:×5", emoji = true)
            value(gson.toJson(ClapActionValue(postId, "clap5")))
            actionId(Const.Action.clap5)
        }
    }
    if (nClaps > 0) {
        section {
            markdownText("${nClaps}拍手！")
        }
    }
}
