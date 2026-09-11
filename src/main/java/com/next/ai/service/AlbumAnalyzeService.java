package com.next.ai.service;

import java.net.URI;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import com.next.ai.vo.album.AlbumAnalyzeResult;

@Service
public class AlbumAnalyzeService {

  private final ChatClient chatClient;

  public AlbumAnalyzeService(@Value("${spring.ai.deepseek.api-key}") String apiKey) {

    OpenAiChatModel model = OpenAiChatModel.builder()
        .options(
            OpenAiChatOptions.builder()
                .baseUrl("https://api.deepseek.com")
                .apiKey(apiKey)
                .model("deepseek-v4-flash-vision-exp")
                .build())
        .build();

    this.chatClient = ChatClient.builder(model).build();
  }

  public AlbumAnalyzeResult analyze(String imageUrl, String mimeType) {

    try {
      var imageResource = new UrlResource(URI.create(imageUrl));
      MimeType mediaType = MimeTypeUtils.parseMimeType(mimeType);

      return chatClient
          .prompt()
          .user(user -> user
              .text("""
                  请分析这张图片，并生成适合相册动态使用的信息。

                  要求：
                  1. title：简短自然，不超过15个汉字。
                  2. detail：一句话描述图片内容，不超过50个汉字。
                  3. 只根据图片中能够确认的内容描述。
                  4. 不确定的人物、地点、时间等信息不要猜测。
                  5. 使用简体中文。
                  6. 如果图片主要内容是食物或饮品：
                     - 尽量识别主要食物名称。
                     - 根据图片中可见的份量、食材和烹饪方式，粗略估算整份食物的总热量。
                     - 在 detail 末尾追加热量，例如：“牛肉咖喱饭配蔬菜，约750 kcal。”
                     - 无法准确判断份量时，给出合理区间，例如：“约600～800 kcal”。
                     - 热量仅为视觉估算，不要表现得过于精确。
                  7. 如果不是食物或饮品，不要输出热量信息。
                  8. items 返回图片中能够区分和识别的所有组成，不限制列表数量，每项必须包含：
                     - name：最可能的名称，简短、具体。
                     - alternativeNames：2～3 个合理的备选名称，不与 name 重复；没有合理备选时返回空数组。
                     - estimatedAmount：可见内容的预估量，例如“约 200 克”“1 碗”“2 个”；无法判断时返回“份量不明”。
                  9. 食物或饮品图片要把所有能够识别的不同食物或饮品分别列出，不要只返回“套餐”“一桌菜”等笼统名称。
                  10. 相同食物的多个重复个体合并为一个 item，但图片中识别出的不同种类都要返回。
                      不要因为坐标标记数量有限而省略 items 中的其他种类。
                      items 的数量不限制为 4，绝不因为 markers 最多 4 条而缩短 items 列表。
                  11. markers 是独立的图片标记列表，最多返回 4 条，每条必须包含：
                      - itemIndex：对应 item 在 items 数组中的下标，从 0 开始。
                      - centerXPercent：对应物品中心点的横向百分比。
                      - centerYPercent：对应物品中心点的纵向百分比。
                      markers 中的坐标必须是 0～100 的数字，不能返回 null；图片左上角是 (0, 0)，右下角是 (100, 100)。
                      如果 items 不超过 4 条，则为每个 item 返回一条 marker；例如 3 个 item 就返回 3 条 marker。
                      如果 items 超过 4 条，优先标记明显、分散、容易定位的物品。
                      很小、过于密集、严重重叠或难以准确标记的物品只降低 marker 选择优先级，不影响它出现在 items 中。
                                    """)
              .media(mediaType, imageResource))
          .call()
          .entity(
              AlbumAnalyzeResult.class,
              spec -> spec.validateSchema());

    } catch (Exception e) {
      throw new RuntimeException(
          "图片分析失败: " + imageUrl,
          e);
    }
  }
}
