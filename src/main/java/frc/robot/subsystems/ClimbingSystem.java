package frc.robot.subsystems;

import static edu.wpi.first.units.Units.Amps;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.MotorConstants;
import frc.robot.Settings.ClimbingSettings;

public class ClimbingSystem extends SubsystemBase {

    private double climbingSpeed;
    //private SparkMax climbingMotor;
    private StatusCode climbingMotorConfig;
    private TalonFX climbingMotor;
    private CurrentLimitsConfigs currentLimitConfigs;

    private double ClimbingPower = 0;

    private DoubleSupplier climbEncoder;

    //private SparkMaxConfig climbMotorConfig;


    public ClimbingSystem() {
        //climbingMotor = new SparkMax(MotorConstants.climbingMotorID, MotorType.kBrushless);
        //climbMotorConfig = new SparkMaxConfig();

        //climbMotorConfig.idleMode(IdleMode.kBrake);

        //climbingMotor.configure(climbMotorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        climbingMotor = new TalonFX(MotorConstants.climbingMotorID);
        //climbingMotor.setControl(climbingMotorConfig);

        // current limit if needed
        currentLimitConfigs = new CurrentLimitsConfigs() 
            .withStatorCurrentLimit(Amps.of(ClimbingSettings.currentLimit))
            .withStatorCurrentLimitEnable(ClimbingSettings.useCurrentLimit);

        climbingMotorConfig = climbingMotor.getConfigurator().apply(currentLimitConfigs);

        climbingMotor.setNeutralMode(NeutralModeValue.Coast);
        

        climbingSpeed = ClimbingSettings.climbingSpeed;
        ClimbingPower = 0;

        climbEncoder = () -> climbingMotor.getPosition().getValueAsDouble() * 1.0/9.0 * 1.0/5.0 * 1.0/5.0 * 2*Math.PI;
    }

    public void update() {
        if (climbEncoder.getAsDouble() > ClimbingSettings.maxEncoderPosition && ClimbingPower > 0) ClimbingPower = 0;
        if (climbEncoder.getAsDouble() < ClimbingSettings.minEncoderPosition && ClimbingPower < 0) ClimbingPower = 0;
        climbingMotor.set(ClimbingPower);
    }


    @Override
    public void periodic() {
        SmartDashboard.putNumber("Climb Motor Current", climbingMotor.getStatorCurrent().getValueAsDouble());
        SmartDashboard.putNumber("Climb Encoder Raw", climbingMotor.getPosition().getValueAsDouble());
        SmartDashboard.putNumber("Climb Encoder", Math.toDegrees(climbEncoder.getAsDouble()));
    }

    @Override
    public void simulationPeriodic() {
        // This method will be called once per scheduler run during simulation
    }

    public void StartClimbing() {
        ClimbingPower = -climbingSpeed;
    }

    public void StartUnclimbing() {
        ClimbingPower = climbingSpeed;
    }

    public void StopClimbing() {
        ClimbingPower = 0;
    }

}