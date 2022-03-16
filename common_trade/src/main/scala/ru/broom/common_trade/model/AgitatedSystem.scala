package ru.broom.common_trade.model

object AgitatedSystem extends Enumeration {
  case class AgitatedLevel(agitatedPercent: Float, levelName: String) extends super.Val {
    def < (that: AgitatedLevel): Boolean = {
      this.agitatedPercent<that.agitatedPercent
    }
    def > (that: AgitatedLevel): Boolean = {
      this.agitatedPercent>that.agitatedPercent
    }
  }

  val NOTHING = AgitatedLevel(0f, "NOTHING")
  val MIN = AgitatedLevel(0.3f, "MIN")
  val LOW = AgitatedLevel(0.5f, "LOW")
  val MID = AgitatedLevel(0.7f, "MID")
  val HIGH = AgitatedLevel(1f, "HIGH")
  val VHIGH = AgitatedLevel(1.2f, "VHIGH")
  val MAX = AgitatedLevel(1.5f, "MAX")

  def identify(percent: Float): AgitatedLevel = {
    if (percent < NOTHING.agitatedPercent)
      throw new Exception("Uncnown agitated value " + percent)
    if (percent < MIN.agitatedPercent) {
      NOTHING
    } else if (percent < LOW.agitatedPercent) {
      MIN
    } else if (percent < MID.agitatedPercent) {
      LOW
    } else if (percent < HIGH.agitatedPercent) {
      MID
    } else if (percent < VHIGH.agitatedPercent) {
      HIGH
    } else if (percent < MAX.agitatedPercent) {
      VHIGH
    } else {
      MAX
    }
  }
}
