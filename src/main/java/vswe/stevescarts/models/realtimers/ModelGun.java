package vswe.stevescarts.models.realtimers;

import java.util.ArrayList;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.helpers.ResourceHelper;
import vswe.stevescarts.models.ModelCartbase;
import vswe.stevescarts.modules.ModuleBase;
import vswe.stevescarts.modules.realtimers.ModuleShooter;

@SideOnly(Side.CLIENT)
public class ModelGun extends ModelCartbase {
	private static ResourceLocation texture;
	ModelRenderer[] guns;

	@Override
	public ResourceLocation getResource(final ModuleBase module) {
		return ModelGun.texture;
	}

	@Override
	protected int getTextureWidth() {
		return 32;
	}

	@Override
	protected int getTextureHeight() {
		return 8;
	}

	public ModelGun() {
	}

	public ModelGun(final ArrayList<Integer> pipes) {
		this.guns = new ModelRenderer[pipes.size()];
		for (int i = 0; i < pipes.size(); ++i) {
			float angle = (new int[] { 3, 4, 5, 2, -1, 6, 1, 0, 7 })[pipes.get(i)];
			angle *= 0.7853982f;
			final ModelRenderer gunAnchorAnchor = new ModelRenderer(this);
			this.AddRenderer(gunAnchorAnchor);
			gunAnchorAnchor.rotateAngleY = angle;
			this.guns[i] = this.createGun(gunAnchorAnchor);
		}
	}

	protected ModelRenderer createGun(final ModelRenderer parent) {
		final ModelRenderer gunAnchor = new ModelRenderer(this);
		parent.addChild(gunAnchor);
		gunAnchor.setRotationPoint(2.5f, 0.0f, 0.0f);
		final ModelRenderer gun = new ModelRenderer(this, 0, 16);
		this.fixSize(gun);
		gunAnchor.addChild(gun);
		gun.addBox(-1.5f, -2.5f, -1.5f, 7, 3, 3, 0.0f);
		gun.setRotationPoint(0.0f, -9.0f, 0.0f);
		return gun;
	}

	@Override
	public void applyEffects(final ModuleBase module, final float yaw, final float pitch, final float roll) {
		for (int i = 0; i < this.guns.length; ++i) {
			this.guns[i].rotateAngleZ = ((module == null) ? 0.0f : ((ModuleShooter) module).getPipeRotation(i));
		}
	}

	static {
		ModelGun.texture = ResourceHelper.getResource("/models/gunModel.png");
	}
}
