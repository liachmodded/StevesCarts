package vswe.stevesvehicles.detector;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import vswe.stevesvehicles.client.gui.detector.DropDownMenuFlow;
import vswe.stevesvehicles.detector.modulestate.ModuleState;
import vswe.stevesvehicles.detector.modulestate.registry.ModuleStateRegistry;
import vswe.stevesvehicles.module.data.registry.ModuleRegistry;
import vswe.stevesvehicles.network.PacketHandler;
import vswe.stevesvehicles.old.Helpers.OperatorObject;
import vswe.stevesvehicles.old.Helpers.ResourceHelper;
import vswe.stevesvehicles.client.gui.screen.GuiDetector;
import vswe.stevesvehicles.module.data.ModuleData;
import vswe.stevesvehicles.module.ModuleBase;
import vswe.stevesvehicles.old.TileEntities.TileEntityDetector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import vswe.stevesvehicles.vehicle.VehicleBase;

public class LogicObject {
	private byte id;
	private LogicObject parent;
	private byte type;
	private ArrayList<LogicObject> children;
	
	private int x;
	private int y;
	private int level;

	private byte data; //TODO shouldn't work with bytes anymore
	
	
	public LogicObject(byte id, byte type, byte data) {
		this.id = id;
		this.type = type;
		this.data = data;
		children = new ArrayList<LogicObject>();
	}	
	
	public LogicObject( byte type, byte data) {
		this((byte)0, type, data);
	}
	
	public void setParent(TileEntityDetector detector, LogicObject parent) {
		if (parent != null) {
			PacketHandler.sendPacket(0, new byte[] {parent.id, getExtra(), data});
			for (LogicObject child : children) {
				child.setParent(detector, this);
			}			
		}else{
			PacketHandler.sendPacket(1,new byte[] {id});
		}
	}
	
	
	
	public void setParent( LogicObject parent) {
		if (this.parent != null) {
			this.parent.children.remove(this);
		}
	
		this.parent = parent;
		if (this.parent != null && this.parent.hasRoomForChild()) {
			this.parent.children.add(this);
		}
	}	

	public ArrayList<LogicObject> getChildren() {
		return children;
	}
	
	public LogicObject getParent() {
		return parent;
	}
	
	public byte getId() {
		return id;
	}
	
	public byte getExtra() {
		return type;
	}

	public byte getData() {
		return data;
	}	
	
	public void setX(int val) {
		this.x = val;
	}
	
	public void setY(int val) {
		this.y = val;
	}	
	
	public void setXCenter(int val) {
		setX(val +(!isOperator() ? -8 : -10));
	}
	
	public void setYCenter(int val) {
		setY(val +(!isOperator() ? -8 : -5));
	}		
	
	@SideOnly(Side.CLIENT)
	public void draw(GuiDetector gui, int mouseX, int mouseY, int x, int y) {
		generatePosition(x - 100/2 ,y, 100,0);		
		draw(gui, mouseX, mouseY);
	}
	
	@SideOnly(Side.CLIENT)
	public void draw(GuiDetector gui, int mouseX, int mouseY) {
		if (!isOperator()) {
			ResourceHelper.bindResource(GuiDetector.TEXTURE);
			
			int xIndex = 0;
			if (gui.inRect(mouseX, mouseY, getRect())) {
				xIndex = 1;
			}
			
			gui.drawTexturedModalRect(gui.getGuiLeft()+ x, gui.getGuiTop() + y , 1 + xIndex * 17, 203, 16, 16);
			
			if (isModule()) {
				ModuleData module = ModuleRegistry.getModuleFromId(data);

				if (module != null) {
                    ResourceHelper.bindResource(GuiDetector.MODULE_TEXTURE);
					gui.drawIcon(module.getIcon(), gui.getGuiLeft() + x, gui.getGuiTop() + y, 1F, 1F, 0F, 0F);
				}
			}else{
				ModuleState state = ModuleStateRegistry.getStateFromId(data);

                if (state != null) {
                    ResourceHelper.bindResource(state.getTexture());
				    gui.drawRectWithTextureSize(gui.getGuiLeft() + x, gui.getGuiTop() + y, 0, 0, 16, 16, 16);
                }
			}
			
		}else{
			ResourceHelper.bindResource(GuiDetector.TEXTURE);
			
			int[] src = DropDownMenuFlow.getSource(gui, data);

			gui.drawTexturedModalRect(gui.getGuiLeft()+x, gui.getGuiTop() + y, src[0], src[1], 20, 11);	

			if (gui.inRect(mouseX, mouseY, getRect())) {
				int xIndex;
				if (gui.currentObject == null) {
					xIndex = 2;
				}else if(hasRoomForChild() && isChildValid(gui.currentObject)) {
					xIndex = 0;
				}else{
					xIndex = 1;
				}
			
				gui.drawTexturedModalRect(gui.getGuiLeft() + x, gui.getGuiTop() + y , 35 + xIndex * 21, 203, 20, 11);
			}			
		}
		
		if (parent != null && parent.maxChilds() > 1) {
			int px1 = gui.getGuiLeft() + x;
			int py1 = gui.getGuiTop() + y;
			int px2 = gui.getGuiLeft() + parent.x;
			int py2 = gui.getGuiTop() + parent.y;
			
			//the middle of the parent
			py2 += 5;

			//the middle of the child
			px1 += (!isOperator() ? 8 : 10);
			
			boolean tooClose = false;
			
			//the right side of the parent
			if (x > parent.x) {
				px2 += 20;
				if (px1 < px2) {
					tooClose = true;
				}
			}else if(px1 > px2) {
				tooClose = true;
			}
			
			if (!tooClose) {
				GuiDetector.drawRect(px1, py2, px2, py2 + 1, 0xFF404040);
                GuiDetector.drawRect(px1, py1, px1 + 1, py2, 0xFF404040);
				GL11.glColor4f(1F, 1F, 1F, 1F);
			}
		}
		
		
		for (LogicObject child : children) {
			child.draw(gui, mouseX, mouseY);
		}		
	}
	
	public void generatePosition(int x, int y, int w, int level) {
		setXCenter(x + w/2);
		setYCenter(y);
		this.level = level;
		
		int max = maxChilds();
		for (int i = 0; i < children.size(); i++) {
			children.get(i).generatePosition(x + (w/max) * i , y+(!children.get(i).isOperator() ? 16 : 11), w/max, level + (children.get(i).maxChilds() > 1 ? 1 : 0));
		}	
	}
	
	private boolean isModule() {
		return type == 0;
	}
	
	private boolean isOperator() {
		return type == 1;
	}	
	
	private boolean isState() {
		return type == 2;
	}	
	
	private OperatorObject getOperator() {
		if (isOperator()) {
			return OperatorObject.getAllOperators().get(data);
		}else{
			return null;
		}
	}	
			
	public boolean evaluateLogicTree(TileEntityDetector detector, VehicleBase vehicle, int depth) {
		if (depth >= 1000) {
			return false;
		}
		
		if (isState()) {
			ModuleState state = ModuleStateRegistry.getStateFromId(getData());
            return state != null && state.isValid(vehicle);
        }else if (isModule()) {
			for (ModuleBase module : vehicle.getModules()) {
				if (getData() == module.getModuleId()) {
					return true;
				}
			}
			return false;
		}else{
			if (getChildren().size() != maxChilds()) {
				return false;
			}
			
			OperatorObject operator = getOperator();
			if (operator != null) {

				
				if (operator.getChildCount() == 2) {
					return operator.evaluate(detector, vehicle, depth + 1, getChildren().get(0), getChildren().get(1));
				}else if(operator.getChildCount() == 1) {
					return operator.evaluate(detector, vehicle, depth + 1, getChildren().get(0) , null);
				}else{
					return operator.evaluate(detector, vehicle, depth + 1, null, null);
				}
				
				
				
			}else{
				return false;
			}
			
		}
	}		
		

	
	
	private int maxChilds() {
		OperatorObject operator = getOperator();
		if (operator != null) {
			return operator.getChildCount();
		}else{
			return 0;
		}
	}

	public boolean isChildValid(LogicObject child) {
		if (level >= 4 && child.isOperator()) {
			return false;
		}else if(level >= 5) {
			return false;
		}
	

		OperatorObject operator = getOperator();
		OperatorObject operatorchild = child.getOperator();
		if (operator != null && operatorchild != null) {
			return operator.isChildValid(operatorchild);
		}else{
			return true;
		}		

	}
	
	public boolean canBeRemoved() {
		OperatorObject operator = getOperator();
		if (operator != null) {
			return operator.inTab();
		}else{
			return true;
		}		
	}
	
	public boolean hasRoomForChild() {
		return children.size() < maxChilds();
	}
	
	public int[] getRect() {
		if (!isOperator()) {
			return new int[] {x, y, 16, 16};
		}else{
			return new int[] {x, y, 20, 11};		
		}
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof LogicObject) {
			LogicObject logic = (LogicObject)obj;
			return logic.id == id && 
					((logic.parent == null && parent == null) || (logic.parent != null && parent != null && logic.parent.id == parent.id)) &&
					logic.getExtra() == getExtra() &&
					logic.getData() == getData();
		}else{
			return false;
		}
	}
	
	public LogicObject copy(LogicObject parent) {
		LogicObject obj = new LogicObject(id, getExtra(), getData());
		obj.setParent(parent);
		return obj;
	}
	
	public String getName() {
		if (isState()) {
			ModuleState state = ModuleStateRegistry.getStateFromId(getData());
			if (state == null) {
				return "Undefined";
			}else{
				return state.getName();
			}
		}else if (isModule()) {
			ModuleData module = ModuleRegistry.getModuleFromId(getData());
			if (module == null) {
				return "Undefined";
			}else{
				return module.getName();
			}
		}else {
			String name = "Undefined";
			
			OperatorObject operator = getOperator();
			if (operator != null) {
				name = operator.getName();
			}			
				
			return name + "\nChild nodes: " + getChildren().size() + "/" + maxChilds();
		}
	}
	
	
}