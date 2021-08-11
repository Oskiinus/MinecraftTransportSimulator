package minecrafttransportsimulator.items.instances;

import minecrafttransportsimulator.blocks.components.ABlockBase;
import minecrafttransportsimulator.blocks.instances.BlockDecor;
import minecrafttransportsimulator.blocks.instances.BlockDecorFluidLoader;
import minecrafttransportsimulator.items.components.AItemSubTyped;
import minecrafttransportsimulator.items.components.IItemBlock;
import minecrafttransportsimulator.jsondefs.JSONDecor;
import minecrafttransportsimulator.jsondefs.JSONDecor.DecorComponentType;

/**Decor item.  Note that while this item can (and does) spawn decor blocks,
 * it can also spawn traffic signal controllers and fuel pumps depending on
 * the definition.  This item, therefore, is essentially a catch-all for all
 * pack, block-based things that aren't poles.
 * 
 * @author don_bruce
 */
public class ItemDecor extends AItemSubTyped<JSONDecor> implements IItemBlock{
	
	public ItemDecor(JSONDecor definition, String subName, String sourcePackID){
		super(definition, subName, sourcePackID);
	}
	
	@Override
	public Class<? extends ABlockBase> getBlockClass(){
		return definition.decor.type.equals(DecorComponentType.FUEL_PUMP) || definition.decor.type.equals(DecorComponentType.FLUID_LOADER) ? BlockDecorFluidLoader.class : BlockDecor.class;
	}
}
