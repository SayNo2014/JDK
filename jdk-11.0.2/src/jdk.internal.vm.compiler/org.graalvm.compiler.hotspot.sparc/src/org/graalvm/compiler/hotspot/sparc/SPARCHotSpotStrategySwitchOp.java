/*
 * Copyright (c) 2015, 2017, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */


package org.graalvm.compiler.hotspot.sparc;

import static jdk.vm.ci.code.ValueUtil.asRegister;
import static jdk.vm.ci.sparc.SPARC.g0;
import static org.graalvm.compiler.asm.sparc.SPARCAssembler.BPCC;
import static org.graalvm.compiler.asm.sparc.SPARCAssembler.CBCOND;
import static org.graalvm.compiler.asm.sparc.SPARCAssembler.INSTRUCTION_SIZE;
import static org.graalvm.compiler.asm.sparc.SPARCAssembler.Annul.ANNUL;
import static org.graalvm.compiler.asm.sparc.SPARCAssembler.BranchPredict.PREDICT_TAKEN;
import static org.graalvm.compiler.lir.sparc.SPARCMove.loadFromConstantTable;

import org.graalvm.compiler.asm.Assembler.LabelHint;
import org.graalvm.compiler.asm.Label;
import org.graalvm.compiler.asm.sparc.SPARCAssembler.CC;
import org.graalvm.compiler.asm.sparc.SPARCAssembler.ConditionFlag;
import org.graalvm.compiler.asm.sparc.SPARCMacroAssembler;
import org.graalvm.compiler.core.common.calc.Condition;
import org.graalvm.compiler.lir.LIRInstructionClass;
import org.graalvm.compiler.lir.LabelRef;
import org.graalvm.compiler.lir.SwitchStrategy;
import org.graalvm.compiler.lir.Variable;
import org.graalvm.compiler.lir.asm.CompilationResultBuilder;
import org.graalvm.compiler.lir.sparc.SPARCControlFlow;
import org.graalvm.compiler.lir.sparc.SPARCDelayedControlTransfer;

import jdk.vm.ci.code.Register;
import jdk.vm.ci.hotspot.HotSpotMetaspaceConstant;
import jdk.vm.ci.meta.AllocatableValue;
import jdk.vm.ci.meta.Constant;
import jdk.vm.ci.meta.Value;
import jdk.vm.ci.sparc.SPARC.CPUFeature;

final class SPARCHotSpotStrategySwitchOp extends SPARCControlFlow.StrategySwitchOp {
    public static final LIRInstructionClass<SPARCHotSpotStrategySwitchOp> TYPE = LIRInstructionClass.create(SPARCHotSpotStrategySwitchOp.class);

    SPARCHotSpotStrategySwitchOp(Value constantTableBase, SwitchStrategy strategy, LabelRef[] keyTargets, LabelRef defaultTarget, AllocatableValue key, Variable scratch) {
        super(TYPE, constantTableBase, strategy, keyTargets, defaultTarget, key, scratch);
    }

    public class HotSpotSwitchClosure extends SwitchClosure {
        protected HotSpotSwitchClosure(Register keyRegister, Register constantBaseRegister, CompilationResultBuilder crb, SPARCMacroAssembler masm) {
            super(keyRegister, constantBaseRegister, crb, masm);
        }

        @Override
        protected void conditionalJump(int index, Condition condition, Label target) {
            if (keyConstants[index] instanceof HotSpotMetaspaceConstant) {
                HotSpotMetaspaceConstant constant = (HotSpotMetaspaceConstant) keyConstants[index];
                CC conditionCode = constant.isCompressed() ? CC.Icc : CC.Xcc;
                ConditionFlag conditionFlag = SPARCControlFlow.fromCondition(true, condition, false);
                LabelHint hint = requestHint(masm, target);

                // Load constant takes one instruction
                int cbCondPosition = masm.position() + INSTRUCTION_SIZE;
                boolean canUseShortBranch = masm.hasFeature(CPUFeature.CBCOND) && SPARCControlFlow.isShortBranch(masm, cbCondPosition, hint, target);

                Register scratchRegister = asRegister(scratch);
                loadFromConstantTable(crb, masm, asRegister(constantTableBase), constant, scratchRegister, SPARCDelayedControlTransfer.DUMMY);

                if (canUseShortBranch) {
                    CBCOND.emit(masm, conditionFlag, conditionCode == CC.Xcc, keyRegister, scratchRegister, target);
                } else {
                    masm.cmp(keyRegister, scratchRegister);
                    BPCC.emit(masm, conditionCode, conditionFlag, ANNUL, PREDICT_TAKEN, target);
                    masm.nop();  // delay slot
                }
            } else {
                super.conditionalJump(index, condition, target);
            }
        }
    }

    @Override
    protected int estimateEmbeddedSize(Constant c) {
        if (c instanceof HotSpotMetaspaceConstant) {
            return ((HotSpotMetaspaceConstant) c).isCompressed() ? 4 : 8;
        } else {
            return super.estimateEmbeddedSize(c);
        }
    }

    @Override
    public void emitCode(final CompilationResultBuilder crb, final SPARCMacroAssembler masm) {
        final Register keyRegister = asRegister(key);
        final Register constantBaseRegister = AllocatableValue.ILLEGAL.equals(constantTableBase) ? g0 : asRegister(constantTableBase);
        strategy.run(new HotSpotSwitchClosure(keyRegister, constantBaseRegister, crb, masm));
    }
}
