package com.atom.shuoshuo.bean.picture;

import com.atom.shuoshuo.bean.picture.Multiimage.MultiImage;
import com.atom.shuoshuo.bean.picture.gifvideo.Gif;
import com.atom.shuoshuo.bean.picture.gifvideo.GifVideoBean;
import com.atom.shuoshuo.bean.picture.singleimage.LongImage;
import com.atom.shuoshuo.bean.picture.singleimage.SingleImage;
import com.atom.shuoshuo.utils.AppUtil;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo.bean.picture
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/8/1  10:42
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class PictureContentDeserializer implements JsonDeserializer<PictureContent> {
    @Override
    public PictureContent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = GsonProvider.gson;
        JsonObject jsonObject = (JsonObject) json;
        final int media_type = intOrEmpty(jsonObject.get("media_type"));
        final int is_multi_image = intOrEmpty(jsonObject.get("is_multi_image"));
        final int is_gif = intOrEmpty(jsonObject.get("is_gif"));
        final JsonObject middle_image = jsonObjectOrEmpty(jsonObject.get("middle_image"));
        final int height = intOrEmpty(middle_image != null ? middle_image.get("r_height") : null);
        final JsonObject gifvideo = jsonObjectOrEmpty(jsonObject.get("gifvideo"));

        PictureContent content = null;
        if (media_type == 1) {  //单张图片
            if (height > AppUtil.getScreenHeight()) {
                content = gson.fromJson(json, LongImage.class);
            } else {
                content = gson.fromJson(json, SingleImage.class);
            }
        } else if (media_type == 2) { //gif图片
            if (is_gif == 1 && gifvideo != null) {
                content = gson.fromJson(json, GifVideoBean.class);
            } else {
                content = gson.fromJson(json, Gif.class);
            }
        } else { //多图
            content = gson.fromJson(json, MultiImage.class);
        }
        return content;
    }

    private int intOrEmpty(JsonElement jsonElement) {
        return jsonElement == null ? 0 : jsonElement.isJsonNull() ? 0 : jsonElement.getAsInt();
    }

    private JsonObject jsonObjectOrEmpty(JsonElement jsonElement) {
        return jsonElement == null ? null : jsonElement.isJsonNull() ? null : jsonElement.getAsJsonObject();
    }

}
