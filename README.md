This is a very patchwork upgrade of IGI to Minecraft 1.12. It uses GitHubLynx's 1.12 update for LunatriusCore, which also needed a tiny bit of updating, but nothing significant enough to merit its own fork.

This is not a final version; however, it is working and stable. There are some issues that need to be worked out, and I will continue my efforts on them:
- com.github.lunatrius.ingameinfo.tag (Tag) -- lines 78-83 are commented out. worldServerForDimension no longer seems to exist. 
- com.github.lunatrius.ingameinfo.tag (TagMisc) -- lines 193 and 194 are currently commented out as they cause an Illegal Access Error. No luck on this so far. My best lead is the fact that Forge patches RenderGlobal.
