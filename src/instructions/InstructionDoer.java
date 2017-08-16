package instructions;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Does what it's told
 * @author BLJames
 *
 */
public class InstructionDoer 
{
	
	private static InstructionDoer SINGLETON = null;
	
	private Robot robot;
	
	private InstructionDoer() throws AWTException
	{
		robot = new Robot();
	}
	
	/**
	 * Gets the instruction doer, so it can do what it's told
	 * @return The instruction doer
	 * @throws AWTException Thrown if CAI can't interact with the system
	 */
	public static InstructionDoer getInstance() throws AWTException
	{
		if(SINGLETON == null)
		{
			SINGLETON = new InstructionDoer();
		}
		return SINGLETON;
	}
	
	/**
	 * Performs the given action based on the interpreted instruction
	 * @param interpretedInstruction The action which has been interpreted from the instruction
	 * @throws IOException Thrown if there is a problem opening a file
	 */
	public void act(Action interpretedInstruction) throws IOException
	{
		List<String> parameters = interpretedInstruction.getParameters();
		switch(interpretedInstruction.getType())
		{
			case MOVE_MOUSE:
			{
				int x = Integer.parseInt(parameters.get(0));
				int y = Integer.parseInt(parameters.get(1));
				robot.mouseMove(x, y);
				break;
			} case CLICK_LEFT:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
					robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
				} else
				{
					new MouseClick(InputEvent.BUTTON1_DOWN_MASK, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case CLICK_MIDDLE:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
					robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
				} else
				{
					new MouseClick(InputEvent.BUTTON1_DOWN_MASK, Integer.parseInt(parameters.get(0))).run();
				}	
				break;
			} case CLICK_RIGHT:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
					robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
				} else
				{
					new MouseClick(InputEvent.BUTTON1_DOWN_MASK, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case HOLD_CLICK_LEFT:
			{
				robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
				break;
			} case HOLD_CLICK_MIDDLE:
			{
				robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
				break;
			} case HOLD_CLICK_RIGHT:
			{
				robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
				break;
			} case RELEASE_CLICK_LEFT:
			{
				robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
				break;
			} case RELEASE_CLICK_MIDDLE:
			{
				robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
				break;
			} case RELEASE_CLICK_RIGHT:
			{
				robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
				break;
			} case SCROLL_MOUSE:
			{
				robot.mouseWheel(Integer.parseInt(parameters.get(0)));
				break;
			} case KEYBOARD_A:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_A);
					robot.keyRelease(KeyEvent.VK_A);
				} else
				{
					new KeyPress(KeyEvent.VK_A, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_B:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_B);
					robot.keyRelease(KeyEvent.VK_B);
				} else
				{
					new KeyPress(KeyEvent.VK_B, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_C:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_C);
					robot.keyRelease(KeyEvent.VK_C);
				} else
				{
					new KeyPress(KeyEvent.VK_C, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_D:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_D);
					robot.keyRelease(KeyEvent.VK_D);
				} else
				{
					new KeyPress(KeyEvent.VK_D, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_E:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_E);
					robot.keyRelease(KeyEvent.VK_E);
				} else
				{
					new KeyPress(KeyEvent.VK_E, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_F:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_F);
					robot.keyRelease(KeyEvent.VK_F);
				} else
				{
					new KeyPress(KeyEvent.VK_F, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_G:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_G);
					robot.keyRelease(KeyEvent.VK_G);
				} else
				{
					new KeyPress(KeyEvent.VK_G, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_H:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_H);
					robot.keyRelease(KeyEvent.VK_H);
				} else
				{
					new KeyPress(KeyEvent.VK_H, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_I:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_I);
					robot.keyRelease(KeyEvent.VK_I);
				} else
				{
					new KeyPress(KeyEvent.VK_I, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_J:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_J);
					robot.keyRelease(KeyEvent.VK_J);
				} else
				{
					new KeyPress(KeyEvent.VK_J, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_K:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_K);
					robot.keyRelease(KeyEvent.VK_K);
				} else
				{
					new KeyPress(KeyEvent.VK_K, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_L:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_L);
					robot.keyRelease(KeyEvent.VK_L);
				} else
				{
					new KeyPress(KeyEvent.VK_L, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_M:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_M);
					robot.keyRelease(KeyEvent.VK_M);
				} else
				{
					new KeyPress(KeyEvent.VK_M, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_N:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_N);
					robot.keyRelease(KeyEvent.VK_N);
				} else
				{
					new KeyPress(KeyEvent.VK_N, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_O:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_O);
					robot.keyRelease(KeyEvent.VK_O);
				} else
				{
					new KeyPress(KeyEvent.VK_O, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_P:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_P);
					robot.keyRelease(KeyEvent.VK_P);
				} else
				{
					new KeyPress(KeyEvent.VK_P, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_Q:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_Q);
					robot.keyRelease(KeyEvent.VK_Q);
				} else
				{
					new KeyPress(KeyEvent.VK_Q, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_R:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_R);
					robot.keyRelease(KeyEvent.VK_R);
				} else
				{
					new KeyPress(KeyEvent.VK_R, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_S:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_S);
					robot.keyRelease(KeyEvent.VK_S);
				} else
				{
					new KeyPress(KeyEvent.VK_S, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_T:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_T);
					robot.keyRelease(KeyEvent.VK_T);
				} else
				{
					new KeyPress(KeyEvent.VK_T, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_U:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_U);
					robot.keyRelease(KeyEvent.VK_U);
				} else
				{
					new KeyPress(KeyEvent.VK_U, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_V:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_V);
					robot.keyRelease(KeyEvent.VK_V);
				} else
				{
					new KeyPress(KeyEvent.VK_V, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_W:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_W);
					robot.keyRelease(KeyEvent.VK_W);
				} else
				{
					new KeyPress(KeyEvent.VK_W, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_X:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_X);
					robot.keyRelease(KeyEvent.VK_X);
				} else
				{
					new KeyPress(KeyEvent.VK_X, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_Y:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_Y);
					robot.keyRelease(KeyEvent.VK_Y);
				} else
				{
					new KeyPress(KeyEvent.VK_Y, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_Z:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_Z);
					robot.keyRelease(KeyEvent.VK_Z);
				} else
				{
					new KeyPress(KeyEvent.VK_Z, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_ZERO:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_0);
					robot.keyRelease(KeyEvent.VK_0);
				} else
				{
					new KeyPress(KeyEvent.VK_0, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_ONE:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_1);
					robot.keyRelease(KeyEvent.VK_1);
				} else
				{
					new KeyPress(KeyEvent.VK_1, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_TWO:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_2);
					robot.keyRelease(KeyEvent.VK_2);
				} else
				{
					new KeyPress(KeyEvent.VK_2, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_THREE:
			{if(parameters == null || parameters.isEmpty())
			{
				robot.keyPress(KeyEvent.VK_3);
				robot.keyRelease(KeyEvent.VK_3);
			} else
			{
				new KeyPress(KeyEvent.VK_3, Integer.parseInt(parameters.get(0))).run();
			}
				break;
			} case KEYBOARD_FOUR:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_4);
					robot.keyRelease(KeyEvent.VK_4);
				} else
				{
					new KeyPress(KeyEvent.VK_4, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_FIVE:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_5);
					robot.keyRelease(KeyEvent.VK_5);
				} else
				{
					new KeyPress(KeyEvent.VK_5, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_SIX:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_6);
					robot.keyRelease(KeyEvent.VK_6);
				} else
				{
					new KeyPress(KeyEvent.VK_6, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_SEVEN:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_7);
					robot.keyRelease(KeyEvent.VK_7);
				} else
				{
					new KeyPress(KeyEvent.VK_7, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_EIGHT:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_8);
					robot.keyRelease(KeyEvent.VK_8);
				} else
				{
					new KeyPress(KeyEvent.VK_8, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_NINE:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_9);
					robot.keyRelease(KeyEvent.VK_9);
				} else
				{
					new KeyPress(KeyEvent.VK_9, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_SPACE:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_SPACE);
					robot.keyRelease(KeyEvent.VK_SPACE);
				} else
				{
					new KeyPress(KeyEvent.VK_SPACE, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_PERIOD:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_PERIOD);
					robot.keyRelease(KeyEvent.VK_PERIOD);
				} else
				{
					new KeyPress(KeyEvent.VK_PERIOD, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_EXCLAMATION:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_EXCLAMATION_MARK);
					robot.keyRelease(KeyEvent.VK_EXCLAMATION_MARK);
				} else
				{
					new KeyPress(KeyEvent.VK_EXCLAMATION_MARK, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_COMMA:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_COMMA);
					robot.keyRelease(KeyEvent.VK_COMMA);
				} else
				{
					new KeyPress(KeyEvent.VK_COMMA, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_COLON:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_COLON);
					robot.keyRelease(KeyEvent.VK_COLON);
				} else
				{
					new KeyPress(KeyEvent.VK_COLON, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_SEMICOLON:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_SEMICOLON);
					robot.keyRelease(KeyEvent.VK_SEMICOLON);
				} else
				{
					new KeyPress(KeyEvent.VK_SEMICOLON, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_LEFT_PAREN:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_LEFT_PARENTHESIS);
					robot.keyRelease(KeyEvent.VK_LEFT_PARENTHESIS);
				} else
				{
					new KeyPress(KeyEvent.VK_LEFT_PARENTHESIS, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_RIGHT_PAREN:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_RIGHT_PARENTHESIS);
					robot.keyRelease(KeyEvent.VK_RIGHT_PARENTHESIS);
				} else
				{
					new KeyPress(KeyEvent.VK_RIGHT_PARENTHESIS, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_LEFT_BRACKET:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_OPEN_BRACKET);
					robot.keyRelease(KeyEvent.VK_OPEN_BRACKET);
				} else
				{
					new KeyPress(KeyEvent.VK_OPEN_BRACKET, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_RIGHT_BRACKET:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_CLOSE_BRACKET);
					robot.keyRelease(KeyEvent.VK_CLOSE_BRACKET);
				} else
				{
					new KeyPress(KeyEvent.VK_CLOSE_BRACKET, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_LEFT_BRACE:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_BRACELEFT);
					robot.keyRelease(KeyEvent.VK_BRACELEFT);
				} else
				{
					new KeyPress(KeyEvent.VK_BRACELEFT, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_RIGHT_BRACE:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_BRACERIGHT);
					robot.keyRelease(KeyEvent.VK_BRACERIGHT);
				} else
				{
					new KeyPress(KeyEvent.VK_BRACERIGHT, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_LESS_THAN:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_SHIFT);
					robot.keyPress(KeyEvent.VK_COMMA);
					robot.keyRelease(KeyEvent.VK_COMMA);
					robot.keyRelease(KeyEvent.VK_SHIFT);
				} else
				{
					new KeyPress(KeyEvent.VK_SHIFT, Integer.parseInt(parameters.get(0))).run();
					new KeyPress(KeyEvent.VK_COMMA, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_GREATER_THAN:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_SHIFT);
					robot.keyPress(KeyEvent.VK_PERIOD);
					robot.keyRelease(KeyEvent.VK_PERIOD);
					robot.keyRelease(KeyEvent.VK_SHIFT);
				} else
				{
					new KeyPress(KeyEvent.VK_SHIFT, Integer.parseInt(parameters.get(0))).run();
					new KeyPress(KeyEvent.VK_PERIOD, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_FORWARD_SLASH:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_SLASH);
					robot.keyRelease(KeyEvent.VK_SLASH);
				} else
				{
					new KeyPress(KeyEvent.VK_SLASH, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_BACK_SLASH:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_BACK_SLASH);
				} else
				{
					new KeyPress(KeyEvent.VK_BACK_SLASH, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_SINGLE_QUOTE:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_QUOTE);
				} else
				{
					new KeyPress(KeyEvent.VK_QUOTE, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_DOUBLE_QUOTE:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_SHIFT);
					robot.keyPress(KeyEvent.VK_QUOTE);
					robot.keyRelease(KeyEvent.VK_QUOTE);
					robot.keyRelease(KeyEvent.VK_SHIFT);
				} else
				{
					new KeyPress(KeyEvent.VK_SHIFT, Integer.parseInt(parameters.get(0))).run();
					new KeyPress(KeyEvent.VK_QUOTE, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_TILDE:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_SHIFT);
					robot.keyPress(KeyEvent.VK_BACK_QUOTE);
					robot.keyRelease(KeyEvent.VK_BACK_QUOTE);
					robot.keyRelease(KeyEvent.VK_SHIFT);
				} else
				{
					new KeyPress(KeyEvent.VK_SHIFT, Integer.parseInt(parameters.get(0))).run();
					new KeyPress(KeyEvent.VK_BACK_QUOTE, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_AT:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_SHIFT);
					robot.keyPress(KeyEvent.VK_2);
				} else
				{
					new KeyPress(KeyEvent.VK_SHIFT, Integer.parseInt(parameters.get(0))).run();
					new KeyPress(KeyEvent.VK_2, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_POUND:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_SHIFT);
					robot.keyPress(KeyEvent.VK_3);
				} else
				{
					new KeyPress(KeyEvent.VK_SHIFT, Integer.parseInt(parameters.get(0))).run();
					new KeyPress(KeyEvent.VK_3, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_DOLLAR:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_SHIFT);
					robot.keyPress(KeyEvent.VK_4);
					robot.keyRelease(KeyEvent.VK_4);
					robot.keyRelease(KeyEvent.VK_SHIFT);
				} else
				{
					new KeyPress(KeyEvent.VK_SHIFT, Integer.parseInt(parameters.get(0))).run();
					new KeyPress(KeyEvent.VK_4, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_PERCENT:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_SHIFT);
					robot.keyPress(KeyEvent.VK_5);
					robot.keyRelease(KeyEvent.VK_5);
					robot.keyRelease(KeyEvent.VK_SHIFT);
				} else
				{
					new KeyPress(KeyEvent.VK_SHIFT, Integer.parseInt(parameters.get(0))).run();
					new KeyPress(KeyEvent.VK_5, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_CARET:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_SHIFT);
					robot.keyPress(KeyEvent.VK_6);
					robot.keyRelease(KeyEvent.VK_6);
					robot.keyRelease(KeyEvent.VK_SHIFT);
				} else
				{
					new KeyPress(KeyEvent.VK_SHIFT, Integer.parseInt(parameters.get(0))).run();
					new KeyPress(KeyEvent.VK_6, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_AMPERSAND:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_SHIFT);
					robot.keyPress(KeyEvent.VK_7);
					robot.keyRelease(KeyEvent.VK_7);
					robot.keyRelease(KeyEvent.VK_SHIFT);
				} else
				{
					new KeyPress(KeyEvent.VK_SHIFT, Integer.parseInt(parameters.get(0))).run();
					new KeyPress(KeyEvent.VK_7, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_ASTERISK:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_SHIFT);
					robot.keyPress(KeyEvent.VK_8);
					robot.keyRelease(KeyEvent.VK_8);
					robot.keyRelease(KeyEvent.VK_SHIFT);
				} else
				{
					new KeyPress(KeyEvent.VK_SHIFT, Integer.parseInt(parameters.get(0))).run();
					new KeyPress(KeyEvent.VK_8, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_DASH:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_MINUS);
				} else
				{
					new KeyPress(KeyEvent.VK_MINUS, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_UNDERSCORE:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_SHIFT);
					robot.keyPress(KeyEvent.VK_MINUS);
					robot.keyRelease(KeyEvent.VK_MINUS);
					robot.keyRelease(KeyEvent.VK_SHIFT);
				} else
				{
					new KeyPress(KeyEvent.VK_SHIFT, Integer.parseInt(parameters.get(0))).run();
					new KeyPress(KeyEvent.VK_MINUS, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_PLUS:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_SHIFT);
					robot.keyPress(KeyEvent.VK_EQUALS);
					robot.keyRelease(KeyEvent.VK_EQUALS);
					robot.keyRelease(KeyEvent.VK_SHIFT);
				} else
				{
					new KeyPress(KeyEvent.VK_SHIFT, Integer.parseInt(parameters.get(0))).run();
					new KeyPress(KeyEvent.VK_EQUALS, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_EQUALS:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_EQUALS);
				} else
				{
					new KeyPress(KeyEvent.VK_EQUALS, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_UP:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_UP);
					robot.keyRelease(KeyEvent.VK_UP);
				} else
				{
					new KeyPress(KeyEvent.VK_UP, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_DOWN:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_DOWN);
					robot.keyRelease(KeyEvent.VK_DOWN);
				} else
				{
					new KeyPress(KeyEvent.VK_DOWN, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_LEFT:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_LEFT);
					robot.keyRelease(KeyEvent.VK_LEFT);
				} else
				{
					new KeyPress(KeyEvent.VK_LEFT, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_RIGHT:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_RIGHT);
					robot.keyRelease(KeyEvent.VK_RIGHT);
				} else
				{
					new KeyPress(KeyEvent.VK_RIGHT, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_F1:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_F1);
				} else
				{
					new KeyPress(KeyEvent.VK_F1, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_F2:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_F2);
				} else
				{
					new KeyPress(KeyEvent.VK_F2, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_F3:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_F3);
				} else
				{
					new KeyPress(KeyEvent.VK_F3, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_F4:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_F4);
				} else
				{
					new KeyPress(KeyEvent.VK_F4, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_F5:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_F5);
				} else
				{
					new KeyPress(KeyEvent.VK_F5, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_F6:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_F6);
				} else
				{
					new KeyPress(KeyEvent.VK_F6, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_F7:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_F7);
				} else
				{
					new KeyPress(KeyEvent.VK_F7, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_F8:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_F8);
				} else
				{
					new KeyPress(KeyEvent.VK_F8, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_F9:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_F9);
				} else
				{
					new KeyPress(KeyEvent.VK_F9, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_F10:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_F10);
				} else
				{
					new KeyPress(KeyEvent.VK_F10, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_F11:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_F11);
				} else
				{
					new KeyPress(KeyEvent.VK_F11, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_F12:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_F12);
				} else
				{
					new KeyPress(KeyEvent.VK_F12, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_F13:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_F13);
				} else
				{
					new KeyPress(KeyEvent.VK_F13, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_F14:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_F14);
				} else
				{
					new KeyPress(KeyEvent.VK_F14, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_F15:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_F15);
				} else
				{
					new KeyPress(KeyEvent.VK_F15, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_F16:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_F16);
				} else
				{
					new KeyPress(KeyEvent.VK_F16, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_F17:
			{if(parameters == null || parameters.isEmpty())
			{
				robot.keyPress(KeyEvent.VK_F17);
			} else
			{
				new KeyPress(KeyEvent.VK_F17, Integer.parseInt(parameters.get(0))).run();
			}
				break;
			} case KEYBOARD_F18:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_F18);
				} else
				{
					new KeyPress(KeyEvent.VK_F18, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_F19:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_F19);
				} else
				{
					new KeyPress(KeyEvent.VK_F19, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_F20:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_F20);
				} else
				{
					new KeyPress(KeyEvent.VK_F20, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_F21:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_F21);
				} else
				{
					new KeyPress(KeyEvent.VK_F21, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_F22:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_F22);
				} else
				{
					new KeyPress(KeyEvent.VK_F22, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_F23:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_F23);
				} else
				{
					new KeyPress(KeyEvent.VK_F23, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_F24:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_F24);
				} else
				{
					new KeyPress(KeyEvent.VK_F24, Integer.parseInt(parameters.get(0))).run();
				}
				break;
			} case KEYBOARD_BACKSPACE:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_BACK_SPACE);
				} else
				{
					new KeyPress(KeyEvent.VK_BACK_SPACE, Integer.parseInt(parameters.get(0)));
				}
				break;
			} case KEYBOARD_SHIFT:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_SHIFT);
				} else
				{
					new KeyPress(KeyEvent.VK_SHIFT, Integer.parseInt(parameters.get(0)));
				}
				break;
			} case KEYBOARD_ENTER:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_ENTER);
				} else
				{
					new KeyPress(KeyEvent.VK_ENTER, Integer.parseInt(parameters.get(0)));
				}
				break;
			} case KEYBOARD_ESC:
			{
				if(parameters == null || parameters.isEmpty())
				{
					robot.keyPress(KeyEvent.VK_ESCAPE);
				} else
				{
					new KeyPress(KeyEvent.VK_ESCAPE, Integer.parseInt(parameters.get(0)));
				}
				break;
			} case OPEN_FILE:
			{
				Desktop.getDesktop().open(new File(parameters.get(0)));
				break;
			} default:
			{
				
			}
		}
	}
	
	private class MouseClick implements Runnable
	{
		
		private int buttonCode;
		private int duration;
		
		MouseClick(int button, int time)
		{
			buttonCode = button;
			duration = time;
		}

		@Override
		public void run() 
		{
			robot.mousePress(buttonCode);
			robot.delay(duration);
			robot.mouseRelease(buttonCode);
		}
		
	}
	
	private class KeyPress implements Runnable
	{
		
		private int keyCode;
		private int duration;
		
		KeyPress(int key, int time)
		{
			keyCode = key;
			duration = time;
		}

		@Override
		public void run() 
		{
			if(duration == 0)
			{
				robot.keyPress(keyCode);
				robot.keyRelease(keyCode);
			} else if(duration == Integer.MAX_VALUE)
			{
				robot.keyPress(keyCode);
			} else if(duration < 0)
			{
				robot.keyRelease(keyCode);
			} else
			{
				robot.keyPress(keyCode);
				robot.delay(duration);
				robot.keyRelease(keyCode);
			}
		}
		
	}
	
}
