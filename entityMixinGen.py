#A script for generating all the redirects and stuff I need to make identities seamless.

cref={
    "Entity.MoveEffect":"Lnet/minecraft/entity/Entity$MoveEffect;",
    "SoundCategory":"Lnet/minecraft/sound/SoundCategory;",
    "SoundEvent":"Lnet/minecraft/sound/SoundEvent;",
    "BlockPos":"Lnet/minecraft/util/math/BlockPos;",
    "BlockState":"Lnet/minecraft/block/BlockState;",
    "PlayerEntity":"Lnet/minecraft/block/BlockState;",
    "Hand":"Lnet/minecraft/util/Hand;",
    "Entity":"Lnet/minecraft/entity/Entity;",
    "EntityPose":"Lnet/minecraft/entity/Entity;",
    "PistonBehavior":"Lnet/minecraft/block/piston/PistonBehavior;",
    "DamageSource":"Lnet/minecraft/entity/damage/DamageSource;",
    "PositionInterpolator":"Lnet/minecraft/entity/PositionInterpolator;",
    "ActionResult":"Lnet/minecraft/util/ActionResult;",
    "boolean":"Z",
    "int":"I",
    "float":"F",
    "void":"V"
}

toRedirectShadow=[
    "Entity.MoveEffect getMoveEffect()",
    "boolean isFlappingWings()"
]
toRedirect=[
    "SoundCategory getSoundCategory()",
    #"SoundEvent getAmbientSound()",
    #"void playAmbientSound()",
    #"SoundEvent getDeathSound()",
    #"SoundEvent getHurtSound(DamageSource source)",
    "PositionInterpolator getInterpolator()",
    "boolean isCollidable(@Nullable Entity entity)",
    #"int getMaxLookPitchChange()",
    #"int getMaxHeadRotation()",
    "boolean collidesWithStateAtPos(BlockPos pos, BlockState state)",
    "boolean shouldSpawnSprintingParticles()",
    "boolean canBeHitByProjectile()",
    "boolean canHit()",
    "boolean isPushable()",
    "boolean isInsideWall()",
    "ActionResult interact(PlayerEntity player, Hand hand)",
    "boolean collidesWith(Entity other)",
    "int getMaxAir()",
    "float getFreezingScale()",
    "boolean isAttackable()",
    "boolean isInvulnerable()",
    "boolean isCustomNameVisible()",
    "float getEyeHeight(EntityPose pose)",
    "float getStandingEyeHeight()",
    "PistonBehavior getPistonBehavior()",
    "boolean canSprintAsVehicle()",
    "float getStepHeight()",
    "boolean canFreeze()",
    "boolean shouldEscapePowderSnow()"
]
partialRedirect=[
    "void onLanding()",
    "void setCustomNameVisible(boolean visible)"
]

def thing(s,tofill,tofillalt=None):
    if(tofillalt==None):
        tofillalt=tofill
    
    s=s.replace("@Nullable ","")
    rettype=s.split(" ")[0]
    s=" ".join(s.split(" ")[1:])
    mname=s.split("(")[0]

    new=""
    if(rettype=="void"):
        new=s[:-1]+", CallbackInfo info)"
    else:
        new=s[:-1]+", CallbackInfoReturnable info)"
    new="private void "+new.replace("(, ","(").replace(mname,mname+"Identity")
    
    s="(".join(s.split("(")[1:])
    s=s[:-1]
    a=[i.split(" ")[-1] for i in s.split(", ")]
    if(a[0]==""):
        a=[]
    s=[i.split(" ")[0] for i in s.split(", ")]
    if(s[0]==""):
        s=[]
    mdescript=(mname+"("+"".join([cref[i] for i in s])+")"+cref[rettype])
    if(rettype!="void"):
        return tofill%(mdescript,new,mname+"("+", ".join(a)+")")
    return tofillalt%(mdescript,new,mname+"("+", ".join(a)+")")
def things(s,tofill,tofillalt=None):
    sstart=s
    if(tofillalt==None):
        tofillalt=tofill
    
    s=s.replace("@Nullable ","")
    rettype=s.split(" ")[0]
    s=" ".join(s.split(" ")[1:])
    mname=s.split("(")[0]

    new=""
    if(rettype=="void"):
        new=s[:-1]+", CallbackInfo info)"
    else:
        new=s[:-1]+", CallbackInfoReturnable info)"
    new="private void "+new.replace("(, ","(").replace(mname,mname+"Identity")
    
    s="(".join(s.split("(")[1:])
    s=s[:-1]
    a=[i.split(" ")[-1] for i in s.split(", ")]
    if(a[0]==""):
        a=[]
    s=[i.split(" ")[0] for i in s.split(", ")]
    if(s[0]==""):
        s=[]
    mdescript=(mname+"("+"".join([cref[i] for i in s])+")"+cref[rettype])
    if(rettype!="void"):
        return tofill%(sstart,mdescript,new,mname+"("+", ".join(a)+")")
    return tofillalt%(sstart,mdescript,new,mname+"("+", ".join(a)+")")

print("//Tons of Redirects - Begin!"+
    "".join([things(trdir,"""
@Shadow
public %s{return null;}
@Inject(method = "%s", at=@At("HEAD"),cancellable=true)
%s{
    if(this.currentIdentity!=null){
        info.setReturnValue(((EntityAccessor)this.currentIdentity).%s);
    }
}
""",
"""
@Shadow
public %s{return;}
@Inject(method = "%s", at=@At("HEAD"),cancellable=true)
%s{
    if(this.currentIdentity!=null){
        ((EntityAccessor)this.currentIdentity).%s;
        info.cancel();
    }
}
""")
    for trdir in toRedirectShadow])
     +"".join([thing(trdir,"""
@Inject(method = "%s", at=@At("HEAD"),cancellable=true)
%s{
    if(this.currentIdentity!=null){
        info.setReturnValue(this.currentIdentity.%s);
    }
}
""",
"""
@Inject(method = "%s", at=@At("HEAD"),cancellable=true)
%s{
    if(this.currentIdentity!=null){
        this.currentIdentity.%s;
        info.cancel();
    }
}
""")
    for trdir in toRedirect])
     +"".join([thing(trdir,"""
@Inject(method = "%s", at=@At("HEAD"))
%s{
    if(this.currentIdentity!=null){
       this.currentIdentity.%s;
    }
}
""")
    for trdir in partialRedirect])
     +"//Tons of Redirects - End"
     )

print("\n\n\n"+"\n".join(["public "+i+";" for i in toRedirectShadow]))
