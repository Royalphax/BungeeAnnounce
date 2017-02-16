package fr.asynchronous.bungeeannounce.handler;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * @author Roytreo28
 */
public enum Executor {
	LINK("open_url", new OpenURLExecutor()),
	CHANGE_PAGE("change_page", new ChangePageExecutor()),
	RUN_COMMAND("run_command", new RunCommandExecutor()),
	OPEN_FILE("open_file", new OpenFileExecutor()),
	SUGGEST_COMMAND("suggest_command", new SuggestCommandExecutor()),
	
	SHOW_TEXT("show_text", new ShowTextExecutor());
	
	private String s;
	private ExecutorAction action;
	
	private Executor(String s, ExecutorAction action)
	{
		this.s = s;
		this.action = action;
	}
	
	public String getString()
	{
		return this.s;
	}
	
	public ExecutorAction getEA()
	{
		return this.action;
	}
	
	public static abstract interface ExecutorAction {
		TextComponent onParse(TextComponent comp, String value);
	}
	
	public static Executor getType(String s)
	{
		for (Executor t : values())
		{
			if (t.getString().equals(s))
			{
				return t;
			}
		}
		return null;
	}
	
	public static class OpenURLExecutor implements ExecutorAction {

		@Override
		public TextComponent onParse(TextComponent comp, String value) {
			comp.setClickEvent(new ClickEvent(Action.OPEN_URL, value));
			return comp;
		}
	}
	
	public static class ChangePageExecutor implements ExecutorAction {

		@Override
		public TextComponent onParse(TextComponent comp, String value) {
			comp.setClickEvent(new ClickEvent(Action.CHANGE_PAGE, value));
			return comp;
		}
	}
	
	public static class RunCommandExecutor implements ExecutorAction {

		@Override
		public TextComponent onParse(TextComponent comp, String value) {
			comp.setClickEvent(new ClickEvent(Action.RUN_COMMAND, value.replace("_", " ")));
			return comp;
		}
	}
	
	public static class OpenFileExecutor implements ExecutorAction {

		@Override
		public TextComponent onParse(TextComponent comp, String value) {
			comp.setClickEvent(new ClickEvent(Action.OPEN_FILE, value));
			return comp;
		}
	}
	
	public static class SuggestCommandExecutor implements ExecutorAction {

		@Override
		public TextComponent onParse(TextComponent comp, String value) {
			comp.setClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, value.replace("_", " ")));
			return comp;
		}
	}
	
	public static class ShowTextExecutor implements ExecutorAction {

		@Override
		public TextComponent onParse(TextComponent comp, String value) {
			comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder((value.replace('_', ' '))).create()));
			return comp;
		}
	}
}
