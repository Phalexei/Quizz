package imag.quizz.common.tool;

public class Pair<A, B> {

    private A a;
    private B b;

    public Pair(final A a, final B b) {
        this.a = a;
        this.b = b;
    }

    public A getA() {
        return this.a;
    }

    public void setA(final A a) {
        this.a = a;
    }

    public B getB() {
        return this.b;
    }

    public void setB(final B b) {
        this.b = b;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final Pair pair = (Pair) o;

        return !(this.a != null ? !this.a.equals(pair.a) : pair.a != null) && !(this.b != null ? !this.b.equals(pair.b) : pair.b != null);
    }

    @Override
    public int hashCode() {
        int result = this.a != null ? this.a.hashCode() : 0;
        result = 31 * result + (this.b != null ? this.b.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "a=" + this.a +
                ", b=" + this.b +
                '}';
    }
}
