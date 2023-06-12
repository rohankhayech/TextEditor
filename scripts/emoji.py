"""
Script used to replace ASCII based emoji strings to proper unicode emojis. 

Author:
    Rohan Khayech
"""

from texteditor.api import ModifyEventHandler

# Report script name
script.setName("Emoji")

class EmojiModifyEventHandler(ModifyEventHandler):
    """
    Text modification event handler for the emoji script.
    """
    def onTextModified(self):
        """
        Called when the edited text is modified by the user.
        """
        replaceEmoji()
        

def replaceEmoji():
    """
    Replaces ":-)" immediately prior to the caret with the smile emoji.
    """
    try:
        text = api.getBefore(3)
        if (text == ":-)"):
            api.replaceText(u"\U0001f60a", 3)
    except ValueError:
        pass # Ignore errors with unpaired surrogates, this script is only concerned with basic ASCII characters.
        

# Register the event handeler
api.registerModifyCallback(EmojiModifyEventHandler())
