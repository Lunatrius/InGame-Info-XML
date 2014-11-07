package com.github.lunatrius.ingameinfo.tag;

import com.github.lunatrius.ingameinfo.reference.Reference;

public abstract class TagIntegration extends Tag {
    public static class TagException extends Exception {
        public TagException(Tag tag, Throwable cause) {
            super(tag.getCategory() + "/" + tag.getName(), cause);
        }
    }

    private boolean logged = false;

    protected void log(TagIntegration tag, Throwable ex) {
        if (!this.logged) {
            Reference.logger.error("Tag error!", new TagException(tag, ex));
            this.logged = true;
        }
    }
}
