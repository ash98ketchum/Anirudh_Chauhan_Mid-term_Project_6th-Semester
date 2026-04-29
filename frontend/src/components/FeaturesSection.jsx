import { useRef } from "react";
import { motion, useScroll, useTransform } from "framer-motion";

const FeatureBlock = ({ title, description, image, reversed }) => {
  const ref = useRef(null);
  const { scrollYProgress } = useScroll({
    target: ref,
    offset: ["start 80%", "end 20%"]
  });

  const yOpacity = useTransform(scrollYProgress, [0, 0.2, 0.8, 1], [0, 1, 1, 0]);
  const yOffset = useTransform(scrollYProgress, [0, 0.2, 0.8, 1], [100, 0, 0, -100]);
  const scaleImg = useTransform(scrollYProgress, [0, 1], [1.1, 1]);

  return (
    <motion.div 
      ref={ref}
      style={{ opacity: yOpacity, y: yOffset }}
      className={`flex flex-col ${reversed ? 'md:flex-row-reverse' : 'md:flex-row'} items-center gap-12 md:gap-24 py-24`}
    >
      {/* Text Content */}
      <div className="flex-1 space-y-6">
        <h2 className="text-4xl md:text-5xl lg:text-6xl font-bold tracking-tight text-white text-balance leading-[1.1]">
          {title}
        </h2>
        <p className="text-lg md:text-xl text-white/60 leading-relaxed max-w-xl">
          {description}
        </p>
      </div>

      {/* Image Container with Parallax inner image */}
      <div className="flex-1 w-full relative aspect-[4/5] overflow-hidden bg-white/5 rounded-2xl">
        <motion.img 
          style={{ scale: scaleImg }}
          src={image} 
          alt={title}
          className="w-full h-full object-cover rounded-2xl"
        />
        {/* Subtle border gradient overlay */}
        <div className="absolute inset-0 border border-white/10 rounded-2xl pointer-events-none" />
      </div>
    </motion.div>
  );
};

export const FeaturesSection = () => {
  const features = [
    {
      title: "Design that speaks.",
      description: "We obsess over every pixel. The animation curves, the typographic hierarchy, the precise amount of whitespace. It's not just a product; it's a statement.",
      image: "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?q=80&w=2564&auto=format&fit=crop",
      reversed: false
    },
    {
      title: "Engineering at the edge.",
      description: "Performance isn't an afterthought. We push the boundaries of what's possible on the web, utilizing bleeding-edge technology to deliver buttery smooth 60fps experiences.",
      image: "https://images.unsplash.com/photo-1550751827-4bd374c3f58b?q=80&w=2670&auto=format&fit=crop",
      reversed: true
    },
    {
      title: "Impact millions.",
      description: "The code you write today shapes the habits of millions tomorrow. We don't just build apps; we build platforms that define entire categories.",
      image: "https://images.unsplash.com/photo-1550745165-9bc0b252726f?q=80&w=2670&auto=format&fit=crop",
      reversed: false
    }
  ];

  return (
    <section className="bg-black py-24 px-6 md:px-12 relative overflow-hidden">
      <div className="max-w-7xl mx-auto">
        {features.map((f, i) => (
          <FeatureBlock key={i} {...f} />
        ))}
      </div>
    </section>
  );
};
