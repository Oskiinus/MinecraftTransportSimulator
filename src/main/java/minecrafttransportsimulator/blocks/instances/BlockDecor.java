package minecrafttransportsimulator.blocks.instances;

import minecrafttransportsimulator.baseclasses.Point3d;
import minecrafttransportsimulator.blocks.components.ABlockBaseTileEntity;
import minecrafttransportsimulator.blocks.tileentities.instances.TileEntityBeacon;
import minecrafttransportsimulator.blocks.tileentities.instances.TileEntityChest;
import minecrafttransportsimulator.blocks.tileentities.instances.TileEntityDecor;
import minecrafttransportsimulator.blocks.tileentities.instances.TileEntityFluidLoader;
import minecrafttransportsimulator.blocks.tileentities.instances.TileEntityFuelPump;
import minecrafttransportsimulator.blocks.tileentities.instances.TileEntityRadio;
import minecrafttransportsimulator.blocks.tileentities.instances.TileEntitySignalController;
import minecrafttransportsimulator.items.instances.ItemDecor;
import minecrafttransportsimulator.jsondefs.JSONDecor.DecorComponentType;
import minecrafttransportsimulator.mcinterface.WrapperNBT;
import minecrafttransportsimulator.mcinterface.WrapperPlayer;
import minecrafttransportsimulator.mcinterface.WrapperWorld;
import minecrafttransportsimulator.systems.PackParserSystem;

public class BlockDecor extends ABlockBaseTileEntity{
	
    public BlockDecor(){
    	super(10.0F, 5.0F);
	}
    
    @Override
	public void onPlaced(WrapperWorld world, Point3d position, WrapperPlayer player){
		//Set placing player for reference.
    	TileEntityDecor tile = world.getTileEntity(position);
    	if(tile != null && tile.definition.decor.type.equals(DecorComponentType.FUEL_PUMP)){
    		((TileEntityFuelPump) tile).placingPlayerID = player.getID();
    	}
	}
    
    @Override
	public TileEntityDecor createTileEntity(WrapperWorld world, Point3d position, WrapperNBT data){
    	ItemDecor item = PackParserSystem.getItem(data);
    	switch(item.definition.decor.type){
			case BEACON: return new TileEntityBeacon(world, position, data);
			case CHEST: return new TileEntityChest(world, position, data);
			case FLUID_LOADER: return new TileEntityFluidLoader(world, position, data);
			case FUEL_PUMP: return new TileEntityFuelPump(world, position, data);
			case GENERIC: return new TileEntityDecor(world, position, data);
			case RADIO: return new TileEntityRadio(world, position, data);
			case SIGNAL_CONTROLLER: return new TileEntitySignalController(world, position, data);
    	}
    	//We'll never get here.
    	return null;
	}
}
