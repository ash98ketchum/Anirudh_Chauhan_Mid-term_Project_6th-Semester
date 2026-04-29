import { useRef, useState } from "react";
import { motion, useScroll, useTransform } from "framer-motion";
import { ArrowUpRight } from "lucide-react";

const PremiumCard = ({ title, category, delay }) => {
  const [mousePosition, setMousePosition] = useState({ x: 0, y: 0 });
  const [isHovered, setIsHovered] = useState(false);
  const cardRef = useRef(null);

  const handleMouseMove = (e) => {
    if (!cardRef.current) return;
    const rect = cardRef.current.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;
    setMousePosition({ x, y });
  };

  return (
    <motion.div
      ref={cardRef}
      onMouseMove={handleMouseMove}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
      initial={{ opacity: 0, y: 50 }}
      whileInView={{ opacity: 1, y: 0 }}
      viewport={{ once: true, margin: "-100px" }}
      transition={{ duration: 0.8, delay, ease: [0.16, 1, 0.3, 1] }}
      className="relative group overflow-hidden bg-[#0a0a0a] border border-white/10 rounded-3xl p-8 aspect-[4/3] flex flex-col justify-end cursor-pointer transition-colors hover:border-white/20"
    >
      {/* Glare effect tracking mouse */}
      <motion.div
        className="pointer-events-none absolute -inset-px opacity-0 group-hover:opacity-100 transition-opacity duration-300"
        style={{
          background: `radial-gradient(600px circle at ${mousePosition.x}px ${mousePosition.y}px, rgba(255,255,255,0.06), transparent 40%)`,
        }}
      />

      <div className="relative z-10 flex justify-between items-end">
        <div>
          <span className="text-white/50 text-xs font-medium uppercase tracking-wider mb-2 block">
            {category}
          </span>
          <h3 className="text-2xl font-bold text-white leading-tight">
            {title}
          </h3>
        </div>
        
        <div className="w-12 h-12 rounded-full border border-white/20 flex items-center justify-center bg-white/5 group-hover:bg-white group-hover:text-black transition-all duration-300 transform group-hover:-translate-y-1 group-hover:translate-x-1">
          <ArrowUpRight size={20} />
        </div>
      </div>
    </motion.div>
  );
};

export const CardsSection = () => {
  const containerRef = useRef(null);
  const { scrollYProgress } = useScroll({
    target: containerRef,
    offset: ["start end", "end start"]
  });

  const y = useTransform(scrollYProgress, [0, 1], [100, -100]);

  const cards = [
    { category: "Engineering", title: "Backend Systems at Scale" },
    { category: "Design", title: "The Anatomy of Premium UI" },
    { category: "Product", title: "Building for Millions" },
    { category: "Culture", title: "Inside Our Milan HQ" },
  ];

  return (
    <section ref={containerRef} className="bg-black py-32 px-6 md:px-12 relative overflow-hidden">
      <div className="max-w-7xl mx-auto">
        <motion.div 
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          className="mb-16 flex flex-col md:flex-row md:items-end justify-between gap-6"
        >
          <div>
            <h2 className="text-4xl md:text-6xl font-bold tracking-tight text-white mb-4">
              Explore our world
            </h2>
            <p className="text-white/60 text-lg max-w-md">
              Dive deep into how we operate, build, and think.
            </p>
          </div>
          
          <button className="px-6 py-3 rounded-full border border-white/20 text-white hover:bg-white hover:text-black transition-colors duration-300 self-start md:self-auto font-medium">
            View All Stories
          </button>
        </motion.div>

        <motion.div 
          style={{ y }}
          className="grid grid-cols-1 md:grid-cols-2 gap-6"
        >
          {cards.map((card, i) => (
            <PremiumCard key={i} {...card} delay={i * 0.1} />
          ))}
        </motion.div>
      </div>
    </section>
  );
};
